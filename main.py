import openpyxl
import requests
import csv
from tqdm import tqdm  # Import tqdm library for progress bar


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
        return response.status_code == 200
    except requests.exceptions.RequestException:
        return False


def main():
    excel_sheet_path = "CMRIT2025Leaderboard.xlsx"
    participants = load_excel_sheet(excel_sheet_path)

    with open('participant_details.csv', mode='w', newline='') as file:
        writer = csv.writer(file)
        writer.writerow(['Handle', 'Codeforces Handle', 'GeeksForGeeks Handle', 'LeetCode Handle', 'CodeChef Handle',
                         'HackerRank Handle', 'Codeforces URL Exists', 'GeeksForGeeks URL Exists', 'LeetCode URL Exists',
                         'CodeChef URL Exists', 'HackerRank URL Exists'])
        print('Handle, Codeforces Handle, GeeksForGeeks Handle, LeetCode Handle, CodeChef Handle, HackerRank Handle, Codeforces URL Exists, GeeksForGeeks URL Exists, LeetCode URL Exists, CodeChef URL Exists, HackerRank URL Exists')

        for participant in tqdm(participants, desc="Checking URLs", unit="participant"):
            handle = participant.handle
            codeforces_handle = participant.codeforces_handle
            geeksforgeeks_handle = participant.geeksforgeeks_handle
            leetcode_handle = participant.leetcode_handle
            codechef_handle = participant.codechef_handle
            hackerrank_handle = participant.hackerrank_handle

            row = [handle, codeforces_handle, geeksforgeeks_handle, leetcode_handle, codechef_handle, hackerrank_handle]

            # Checking if URLs exist for each handle
            codeforces_url_exists = check_url_exists(f"https://codeforces.com/profile/{codeforces_handle}") if codeforces_handle != '#N/A' else ''
            geeksforgeeks_url_exists = check_url_exists(f"https://auth.geeksforgeeks.org/user/{geeksforgeeks_handle}/") if geeksforgeeks_handle != '#N/A' else ''
            leetcode_url_exists = check_url_exists(f"https://leetcode.com/{leetcode_handle}/") if leetcode_handle != '#N/A' else ''
            codechef_url_exists = check_url_exists(f"https://www.codechef.com/users/{codechef_handle}") if codechef_handle != '#N/A' else ''
            hackerrank_url_exists = check_url_exists(f"https://www.hackerrank.com/profile/{hackerrank_handle}") if hackerrank_handle != '#N/A' else ''

            row.extend([codeforces_url_exists, geeksforgeeks_url_exists, leetcode_url_exists, codechef_url_exists, hackerrank_url_exists])
            print('{},{},{},{},{},{},{},{},{},{},{}'.format(handle, codeforces_handle, geeksforgeeks_handle, leetcode_handle, codechef_handle, hackerrank_handle, codeforces_url_exists, geeksforgeeks_url_exists, leetcode_url_exists, codechef_url_exists, hackerrank_url_exists))

            writer.writerow(row)


if __name__ == "__main__":
    main()
