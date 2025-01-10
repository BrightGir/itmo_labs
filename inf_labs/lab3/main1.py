import re

pattern = r":<\)"
def smile_count(str):
    return len(re.findall(pattern, str))

test1 = input()
print(smile_count(test1))
