import cloudscraper
import urllib.parse
import time
import sqlite3
from ratelimiter import RateLimiter

def fetch_true_leetcode_handles(db_name):
    true_leetcode = []

    try:
        # Establish database connection
        conn = sqlite3.connect(db_name)
        cursor = conn.cursor()

        # Execute SQL query to fetch true leetcode handles
        cursor.execute("SELECT handle, leetcode_handle FROM users_data WHERE leetcode_url_exists = 1")
        rows = cursor.fetchall()

        # Iterate over the result set and add true leetcode handles to the list
        for row in rows:
            handle, leetcode_handle = row
            if leetcode_handle is not None:
                true_leetcode.append((handle, leetcode_handle))

    except sqlite3.Error as e:
        print("Error fetching true Leetcode handles:", e)
    finally:
        if conn:
            conn.close()

    return true_leetcode

def scrape_leetcode(true_leetcode):
    print("Leetcode scraping in progress...")

    # Create or clear the file for writing
    with open("leetcode_ratings.txt", "w") as file:
        file.write("")

    counter = 1
    size = len(true_leetcode)

    scraper = cloudscraper.create_scraper()
    
    # Rate limit the function to a maximum of 2 requests per second
    limiter = RateLimiter(max_calls=MAX_REQUESTS_PER_MINUTE, period=1)

    for handle, leetcode_handle in true_leetcode:
        # Construct URL for API request
        encoded_leetcode_handle = urllib.parse.quote(leetcode_handle, safe='')
        url = LEETCODE_URL.replace("{<username>}", encoded_leetcode_handle)
        url = url.replace(" ", "%20")
        try:
            with limiter:
                print("URL:", url)
                response = scraper.get(url)

                # Handle specific response codes
                if response.status_code == 404 or response.status_code == 400:
                    if response.status_code == 524:
                        # Wait and retry for response code 524
                        time.sleep(30)
                        continue
                    else:
                        raise RuntimeError("Error fetching data, status code:", response.status_code)

                # Parse JSON response
                json_content = response.json()

                try:
                    # Get rating from JSON response
                    rating = json_content['data']['userContestRanking']['rating']
                except TypeError:
                    # Handle NoneType error
                    print(f"Rating for {handle} with leetcode handle {leetcode_handle} not found.")
                    rating = 0

                # Print rating information
                print(f"({counter}/{size}) Leetcode rating for {handle} with leetcode handle {leetcode_handle} is: {rating}")

                # Write to text file
                with open("leetcode_ratings.txt", "a") as file:
                    # round(rating) is used to round off the rating to the nearest integer
                    file.write(f"{handle},{leetcode_handle},{round(rating)}\n")

                counter += 1

        except Exception as e:
            # Error handling
            raise RuntimeError(f"Error fetching leetcode rating for {handle} with leetcode handle {leetcode_handle}: {e}")

    print("Leetcode scraping completed.")

# Constants
LEETCODE_URL = '''
https://leetcode.com/graphql?query=query{userContestRanking(username:"{<username>}"){rating}}
'''
MAX_REQUESTS_PER_MINUTE = 2

# Main function
def main():
    true_leetcode_handles = fetch_true_leetcode_handles("cmrit")
    scrape_leetcode(true_leetcode_handles)

if __name__ == "__main__":
    main()
