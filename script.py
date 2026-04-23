import os
import requests

def read_file(path):
    try:
        with open(path, 'r', encoding='utf-8') as f:
            return f.read()
    except:
        return ""

diff = read_file("diff.txt")
files = read_file("files.txt")

if not diff.strip():
    summary = "No changes detected."
else:
    prompt = f"""
You are a senior software engineer.

Analyze the following git changes.

Return:
1. Summary
2. Feature / bug fix description
3. Files changed and why
4. Risks
5. Suggested tests

Changed files:
{files}

Diff:
{diff[:12000]}
"""

    response = requests.post(
        "https://api.openai.com/v1/chat/completions",
        headers={
            "Authorization": "Bearer " + os.environ["OPENAI_API_KEY"],
            "Content-Type": "application/json"
        },
        json={
            "model": "gpt-4.1-mini",
            "messages": [
                {"role": "system", "content": "You summarize code changes."},
                {"role": "user", "content": prompt}
            ]
        }
    )

    print("STATUS:", response.status_code)
    print("RESPONSE:", response.text)

    try:
        summary = response.json()["choices"][0]["message"]["content"]
    except Exception as e:
        summary = f"Failed to parse response: {e}"

with open("summary.txt", "w", encoding="utf-8") as f:
    f.write(summary)