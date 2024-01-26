import requests

def check_hackerrank_profile_exists(username):
    api_url = f"https://www.hackerrank.com/rest/hackers/{username}/"
    headers = {
        "User-agent": "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.80 Safari/537.36"
    }
    response = requests.get(api_url, headers=headers)
    return response

# Example usage:
username = "21r01a7223"
print(check_hackerrank_profile_exists(username))
