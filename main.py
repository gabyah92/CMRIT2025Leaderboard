import openpyxl
import requests
import csv
from tqdm import tqdm  # Import tqdm library for progress bar
from time import sleep


class Participant:
    # Class to store participant details
    def __init__(self, handle, codeforces_handle, geeksforgeeks_handle, leetcode_handle, codechef_handle,
                 hackerrank_handle):
        # type cast all handles to string
        handle = str(handle)
        codeforces_handle = str(codeforces_handle)
        geeksforgeeks_handle = str(geeksforgeeks_handle)
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
        handle, codeforces_handle, geeksforgeeks_handle, leetcode_handle, codechef_handle, hackerrank_handle = row
        participants.append(
            Participant(handle, codeforces_handle, geeksforgeeks_handle, leetcode_handle, codechef_handle,
                        hackerrank_handle))

    return participants


def check_url_exists(url):
    header = {
        "User-agent": "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.80 Safari/537.36"
    }
    try:
        response = requests.get(url, headers=header)
        if response.status_code == 200:
            # Check if the final URL is the same as the original URL (no redirect)
            if "leetcode.com" in url:
                url = url + "/"
                if response.url == url:
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
        writer.writerow(['Handle', 'Codeforces Handle', 'GeeksForGeeks Handle', 'LeetCode Handle', 'CodeChef Handle',
                         'HackerRank Handle', 'Codeforces URL Exists', 'GeeksForGeeks URL Exists', 'LeetCode URL Exists',
                         'CodeChef URL Exists', 'HackerRank URL Exists'])
        print('Handle, Codeforces Handle, GeeksForGeeks Handle, LeetCode Handle, CodeChef Handle, HackerRank Handle, '
              'Codeforces URL Exists, GeeksForGeeks URL Exists, LeetCode URL Exists, CodeChef URL Exists, HackerRank '
              'URL Exists')

        log_writer = open('log.txt', 'a')

        for participant in tqdm(participants, desc="Checking URLs", unit="participant"):
            handle = participant.handle
            codeforces_handle = participant.codeforces_handle
            geeksforgeeks_handle = participant.geeksforgeeks_handle
            leetcode_handle = participant.leetcode_handle
            codechef_handle = participant.codechef_handle
            hackerrank_handle = participant.hackerrank_handle

            row = [handle, codeforces_handle, geeksforgeeks_handle, leetcode_handle, codechef_handle, hackerrank_handle]

            # Checking if URLs exist for each handle
            if codeforces_handle != '#N/A':
                codeforces_url_exists, response_url = check_url_exists("https://codeforces.com/profile/" + codeforces_handle)
            else:
                codeforces_url_exists = False
                response_url = "N/A"

            # Write to log.txt, all details of participant
            log_writer.write("Codeforces Handle: " + codeforces_handle + "\n" + "Codeforces URL: " + "https://codeforces.com/profile/" + codeforces_handle + "\n" + "Response URL: " + response_url + "\n" + "Codeforces URL Exists: " + str(codeforces_url_exists) + "\n\n")

            if geeksforgeeks_handle != '#N/A':
                geeksforgeeks_url_exists, response_url = check_url_exists("https://auth.geeksforgeeks.org/user/" + geeksforgeeks_handle)
            else:
                geeksforgeeks_url_exists = False
                response_url = "N/A"

            # Write to log.txt, all details of participant
            log_writer.write("GeeksForGeeks Handle: " + geeksforgeeks_handle + "\n" + "GeeksForGeeks URL: " + "https://auth.geeksforgeeks.org/user/" + geeksforgeeks_handle + "\n" + "Response URL:" + response_url + "\n" + "GeeksForGeeks URL Exists: " + str(geeksforgeeks_url_exists) + "\n\n")

            if leetcode_handle != '#N/A':
                leetcode_url_exists, response_url = check_url_exists("https://leetcode.com/" + leetcode_handle)
            else:
                leetcode_url_exists = False
                response_url = "N/A"

            # Write to log.txt, all details of participant
            log_writer.write("LeetCode Handle: " + leetcode_handle + "\n" + "LeetCode URL: " + "https://leetcode.com/" + leetcode_handle + "\n" + "Response URL:" + response_url + "\n" + "LeetCode URL Exists: " + str(leetcode_url_exists) + "\n\n")

            if codechef_handle != '#N/A':
                codechef_url_exists, response_url = check_url_exists("https://www.codechef.com/users/" + codechef_handle)
            else:
                codechef_url_exists = False
                response_url = "N/A"

            # Write to log.txt, all details of participant
            log_writer.write("CodeChef Handle: " + codechef_handle + "\n" + "CodeChef URL: " + "https://www.codechef.com/users/" + codechef_handle + "\n" + "Response URL:" + response_url + "\n" + "CodeChef URL Exists: " + str(codechef_url_exists) + "\n\n")

            if hackerrank_handle != '#N/A':
                hackerrank_url_exists, response_url = check_url_exists("https://www.hackerrank.com/profile/" + hackerrank_handle)
            else:
                hackerrank_url_exists = False
                response_url = "N/A"

            # Write to log.txt, all details of participant
            log_writer.write("HackerRank Handle: " + hackerrank_handle + "\n" + "HackerRank URL: " + "https://www.hackerrank.com/profile/" + hackerrank_handle + "\n" + "Response URL:" + response_url + "\n" + "HackerRank URL Exists: " + str(hackerrank_url_exists) + "\n\n")

            row.extend([codeforces_url_exists, geeksforgeeks_url_exists, leetcode_url_exists, codechef_url_exists, hackerrank_url_exists])
            print('{},{},{},{},{},{},{},{},{},{},{}'.format(handle, codeforces_handle, geeksforgeeks_handle, leetcode_handle, codechef_handle, hackerrank_handle, codeforces_url_exists, geeksforgeeks_url_exists, leetcode_url_exists, codechef_url_exists, hackerrank_url_exists))

            log_writer.write("================================================================================================================================================================================\n")

            writer.writerow(row)


if __name__ == "__main__":
    main()
