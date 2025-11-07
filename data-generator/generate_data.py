#!/usr/bin/env python3
"""
Utility script to prepopulate the benchmark database with a large data set and
generate auxiliary files for JMeter. When run it will insert 2 000 categories
and 100 000 items (~50 per category) into the configured PostgreSQL database.
It also writes CSV files containing existing identifiers for categories and
items, and creates example JSON payloads used in POST/PUT requests. Run this
script once before executing the benchmarks.

The connection parameters are read from environment variables. You can
override the defaults using DB_HOST, DB_PORT, DB_NAME, DB_USER and
DB_PASSWORD.
"""

import csv
import os
import random
import string
from datetime import datetime
import psycopg2

# Configuration constants
NUM_CATEGORIES = 2000
NUM_ITEMS_TOTAL = 100_000
ITEMS_PER_CATEGORY = NUM_ITEMS_TOTAL // NUM_CATEGORIES

# Read DB connection details from environment variables or fallback to defaults
DB_HOST = os.environ.get("DB_HOST", "localhost")
DB_PORT = int(os.environ.get("DB_PORT", "5432"))
DB_NAME = os.environ.get("DB_NAME", "perf")
DB_USER = os.environ.get("DB_USER", "perf")
DB_PASSWORD = os.environ.get("DB_PASSWORD", "perf")

# Output directory for JMeter data
OUTPUT_DIR = os.path.join(os.path.dirname(__file__), "..", "jmeter-data")


def rand_text(kb: int) -> str:
    """Return a pseudo‑random ASCII string of approximately kb kilobytes."""
    size = kb * 1024
    # Use letters and spaces to mimic normal text. The default random
    # implementation is sufficiently fast for our purposes.
    return ''.join(random.choices(string.ascii_letters + ' ', k=size))


def main():
    os.makedirs(OUTPUT_DIR, exist_ok=True)

    print(f"Connecting to PostgreSQL at {DB_HOST}:{DB_PORT}/{DB_NAME} as {DB_USER}")
    conn = psycopg2.connect(host=DB_HOST, port=DB_PORT, dbname=DB_NAME,
                            user=DB_USER, password=DB_PASSWORD)
    cur = conn.cursor()

    # Reset tables to avoid duplicate inserts when running multiple times
    print("Truncating existing tables…")
    cur.execute("TRUNCATE TABLE items RESTART IDENTITY CASCADE;")
    cur.execute("TRUNCATE TABLE categories RESTART IDENTITY CASCADE;")
    conn.commit()

    # Insert categories
    print(f"Inserting {NUM_CATEGORIES} categories…")
    categories = [(f"Category {i+1}", rand_text(1)[:500]) for i in range(NUM_CATEGORIES)]
    cur.executemany("INSERT INTO categories(name, description) VALUES (%s, %s)", categories)
    conn.commit()

    # Retrieve generated category ids to assign to items
    cur.execute("SELECT id FROM categories ORDER BY id")
    category_ids = [row[0] for row in cur.fetchall()]

    # Prepare and insert items in batches to avoid overwhelming the DB
    print(f"Inserting {NUM_ITEMS_TOTAL} items…")
    items_batch = []
    total_inserted = 0
    for cat_id in category_ids:
        for i in range(ITEMS_PER_CATEGORY):
            name = f"Item {cat_id}-{i+1}"
            price = round(random.uniform(1, 1000), 2)
            stock = random.randint(0, 10000)
            description = None  # heavy descriptions are added via payloads in the benchmark
            items_batch.append((cat_id, name, price, stock, description))
            if len(items_batch) >= 5000:
                cur.executemany(
                    "INSERT INTO items(category_id, name, price, stock, description) VALUES (%s, %s, %s, %s, %s)",
                    items_batch
                )
                conn.commit()
                total_inserted += len(items_batch)
                print(f"  inserted {total_inserted}/{NUM_ITEMS_TOTAL} items…")
                items_batch.clear()
    # Insert remaining items
    if items_batch:
        cur.executemany(
            "INSERT INTO items(category_id, name, price, stock, description) VALUES (%s, %s, %s, %s, %s)",
            items_batch
        )
        conn.commit()
        total_inserted += len(items_batch)
        print(f"  inserted {total_inserted}/{NUM_ITEMS_TOTAL} items…")
        items_batch.clear()

    # Generate CSV files with ids for JMeter
    print("Generating CSV id lists for JMeter…")
    with open(os.path.join(OUTPUT_DIR, "categories_ids.csv"), "w", newline="") as f:
        writer = csv.writer(f)
        for cid in category_ids:
            writer.writerow([cid])
    cur.execute("SELECT id, category_id FROM items ORDER BY id")
    with open(os.path.join(OUTPUT_DIR, "items_ids.csv"), "w", newline="") as f:
        writer = csv.writer(f)
        for row in cur.fetchall():
            writer.writerow(row)

    # Create example JSON payloads for light and heavy item bodies
    print("Creating example payloads…")
    light_description = rand_text(1)[:-200]  # ensure roughly 1 KB total when combined with field names
    with open(os.path.join(OUTPUT_DIR, "item_payload_1kb.json"), "w") as f:
        f.write(f'{{"name":"LightItem","price":10.5,"stock":42,"categoryId":1,"description":"{light_description}"}}')
    heavy_description = rand_text(5)
    with open(os.path.join(OUTPUT_DIR, "item_payload_5kb.json"), "w") as f:
        f.write(f'{{"name":"HeavyItem","price":99.9,"stock":123,"categoryId":1,"description":"{heavy_description}"}}')

    print("Data generation completed at", datetime.now())
    cur.close()
    conn.close()


if __name__ == "__main__":
    main()