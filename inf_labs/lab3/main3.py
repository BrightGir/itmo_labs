import re
import json


pattern = (r"(^[бвгджзклмнпрстфьхцчшщй]*([аеёиоуыэюя])(([\-]?\2?[бвгджзклмьнпрстфхцчшщй])|([\-]?[бвгджзклмнпрстфхцчшщй]?\2))*)[.,]?$")


t = input("Введите слова:\n")
t += ' '
my_json = {}
my_answers = []
for x in t.split(' '):
    m = re.findall(pattern,x)
    if m:
        my_answers.append(m[0][0])
my_answers.sort(key=lambda x:(len(x),x))
st = ''
for x in my_answers:
    print(x)
    st += x + ' '
st = st[:-1]
my_json["answers"] = st
with open('result.json', 'w', encoding="utf-8") as file:
    dumped_json = json.dumps(my_json, ensure_ascii=False)
    file.write(dumped_json)