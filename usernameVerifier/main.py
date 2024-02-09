import openpyxl
import requests
import csv
from bs4 import BeautifulSoup
from tqdm import tqdm  # Import tqdm library for progress bar


class Participant:
    # Class to store participant details
    def __init__(self, handle, geeksforgeeks_handle, codeforces_handle, leetcode_handle, codechef_handle,
                 hackerrank_handle):
        # type cast all handles to string
        handle = str(handle)
        geeksforgeeks_handle = str(geeksforgeeks_handle)
        codeforces_handle = str(codeforces_handle)
        leetcode_handle = str(leetcode_handle)
        codechef_handle = str(codechef_handle)
        hackerrank_handle = str(hackerrank_handle)

        self.handle = handle
        self.codeforces_handle = codeforces_handle
        self.geeksforgeeks_handle = geeksforgeeks_handle
        self.leetcode_handle = leetcode_handle
        self.codechef_handle = codechef_handle
        if hackerrank_handle.startswith('@'):
            hackerrank_handle = hackerrank_handle[1:]
        self.hackerrank_handle = hackerrank_handle


def load_excel_sheet(excel_sheet_path):
    # Function to load the excel sheet and return a list of Participant objects
    participants = []

    workbook = openpyxl.load_workbook(excel_sheet_path)
    sheet = workbook.active

    for row in tqdm(sheet.iter_rows(min_row=2, values_only=True), desc="Processing Participants", unit="participant"):
        # replace spaces with empty string
        row = [str(x).strip() for x in row]
        handle, geeksforgeeks_handle, codeforces_handle, leetcode_handle, codechef_handle, hackerrank_handle = row
        participants.append(
            Participant(handle, geeksforgeeks_handle, codeforces_handle, leetcode_handle, codechef_handle,
                        hackerrank_handle))

    return participants


def check_url_exists(url):
    # if url is leeetcode
    if url.startswith("https://leetcode.com/"):
        try:
            response = requests.get(url)
            if response.status_code == 200:
                return True, response.url
            return False, response.url
        except requests.exceptions.RequestException:
            return False, "Exception"
    header = {
        "User-agent": "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.80 "
                      "Safari/537.36"
    }
    # if url is hackerrank
    if url.startswith("https://www.hackerrank.com/"):
        try:
            response = requests.get(url, headers=header)
            soup = BeautifulSoup(response.text, 'html.parser')
            # Extract the title of the page
            title = soup.title.string
            # Hackerrank handles that do not exist have the title "HackerRank" in them
            # If user exists, title will be " Name - User Profile | HackerRank"
            if title.startswith("HackerRank"):
                return False, response.url
            return True, response.url
        except requests.exceptions.RequestException:
            return False, "Exception"
    try:
        response = requests.get(url, headers=header)
        if response.status_code == 200:
            # Check if the final URL is the same as the original URL (no redirect), if redirected, then URL does not
            # exist codeforces redirect is found by checking if final url is https://codeforces.com/ geeksforgeeks
            # redirect is found by checking if final url is
            # https://auth.geeksforgeeks.org/?to=https://auth.geeksforgeeks.org/profile.php codechef redirect is
            # found by checking if final url is https://www.codechef.com/ Hackerrank and Leetcode return 404 error if
            # handle does not exist
            if response.url == "https://codeforces.com/" or response.url == "https://auth.geeksforgeeks.org/?to=https://auth.geeksforgeeks.org/profile.php" or response.url == "https://www.codechef.com/":
                return False, response.url
            else:
                return True, response.url
        return False, response.url
    except requests.exceptions.RequestException:
        return False, "Exception"


def main():
    excel_sheet_path = "CMRIT2025Leaderboard.xlsx"
    participants = load_excel_sheet(excel_sheet_path)
    # if log.txt exists, delete it
    try:
        open('log.txt', 'r')
        open('log.txt', 'w').close()
    except FileNotFoundError:
        pass

    with open('participant_details.csv', mode='w', newline='') as file:
        writer = csv.writer(file)
        writer.writerow(['Handle', 'GeeksForGeeks Handle', 'Codeforces Handle', 'LeetCode Handle', 'CodeChef Handle',
                         'HackerRank Handle', 'GeeksForGeeks URL Exists', 'Codeforces URL Exists',
                         'LeetCode URL Exists',
                         'CodeChef URL Exists', 'HackerRank URL Exists'])

        log_writer = open('log.txt', 'a')

        for participant in tqdm(participants, desc="Checking URLs", unit="participant"):
            handle = participant.handle
            geeksforgeeks_handle = participant.geeksforgeeks_handle
            codeforces_handle = participant.codeforces_handle
            leetcode_handle = participant.leetcode_handle
            codechef_handle = participant.codechef_handle
            hackerrank_handle = participant.hackerrank_handle

            row = [handle, geeksforgeeks_handle, codeforces_handle, leetcode_handle, codechef_handle, hackerrank_handle]

            if geeksforgeeks_handle != '#N/A':
                geeksforgeeks_url_exists, response_url = check_url_exists(
                    "https://auth.geeksforgeeks.org/user/" + geeksforgeeks_handle)
            else:
                geeksforgeeks_url_exists = False
                response_url = "N/A"

            # Write to log.txt, all details of participant
            log_writer.write(
                f"GeeksForGeeks Handle: {geeksforgeeks_handle}\nGeeksForGeeks URL: https://auth.geeksforgeeks.org/user/{geeksforgeeks_handle}\nResponse URL: {response_url}\nGeeksForGeeks URL Exists: {geeksforgeeks_url_exists}\n\n")

            # Checking if codeforces handle exists
            if codeforces_handle != '#N/A':
                codeforces_url_exists, response_url = check_url_exists(
                    "https://codeforces.com/profile/" + codeforces_handle)
                if response_url != "https://codeforces.com/profile/" + codeforces_handle and codeforces_url_exists == True:
                    # set codeforces_handle to the handle in the URL
                    codeforces_handle = response_url.split('/')[-1]
            else:
                codeforces_url_exists = False
                response_url = "N/A"

            # Write to log.txt, all details of participant for codeforces
            log_writer.write(
                f"Handle: {handle}\nCodeforces Handle: {codeforces_handle}\nCodeforces URL: https://codeforces.com/profile/{codeforces_handle}\nResponse URL: {response_url}\nCodeforces URL Exists: {codeforces_url_exists}\n\n")

            # Checking if leetcode handle exists
            if leetcode_handle != '#N/A':
                leetcode_url_exists, response_url = check_url_exists("https://leetcode.com/" + leetcode_handle + "/")
            else:
                leetcode_url_exists = False
                response_url = "N/A"

            # Write to log.txt, all details of participant for leetcode
            log_writer.write(
                f"LeetCode Handle: {leetcode_handle}\nLeetCode URL: https://leetcode.com/{leetcode_handle}\nResponse URL: {response_url}\nLeetCode URL Exists: {leetcode_url_exists}\n\n")

            # Checking if codechef handle exists
            if codechef_handle != '#N/A':
                codechef_url_exists, response_url = check_url_exists(
                    "https://www.codechef.com/users/" + codechef_handle)
            else:
                codechef_url_exists = False
                response_url = "N/A"

            # Write to log.txt, all details of participant for codechef
            log_writer.write(
                f"CodeChef Handle: {codechef_handle}\nCodeChef URL: https://www.codechef.com/users/{codechef_handle}\nResponse URL: {response_url}\nCodeChef URL Exists: {codechef_url_exists}\n\n")

            # Checking if hackerrank handle exists
            if hackerrank_handle != '#N/A':
                hackerrank_url_exists, response_url = check_url_exists(
                    "https://www.hackerrank.com/profile/" + hackerrank_handle)
            else:
                hackerrank_url_exists = False
                response_url = "N/A"

            # Write to log.txt, all details of participant for hackerrank
            log_writer.write(
                f"HackerRank Handle: {hackerrank_handle}\nHackerRank URL: https://www.hackerrank.com/profile/{hackerrank_handle}\nResponse URL: {response_url}\nHackerRank URL Exists: {hackerrank_url_exists}\n\n")

            row.extend([geeksforgeeks_url_exists, codeforces_url_exists, leetcode_url_exists, codechef_url_exists,
                        hackerrank_url_exists])

            log_writer.write(
                "================================================================================================================================================================================\n")

            writer.writerow(row)


if __name__ == "__main__":
    main()
