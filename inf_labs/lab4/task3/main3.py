import re
import time
res = ''
pattern = r'<(\w+)>(.*?)<\/\1>'
def parse(tag:str, content:str, level:int):
    if content.count('<') == 0:
        return ('  '*level + tag + ": " + content)
    s = ''
    matches = re.findall(pattern,content)
    s += '  ' * level + tag + ':\n'
    for i in range(0,len(matches)):
        s += parse(matches[i][0],matches[i][1],level+1)
        if (i != len(matches) - 1):
            s += '\n'
    return s

inp = open('schedule.xml', "r", encoding="utf-8")
out = open("schedule.yaml", "w", encoding='utf-8')


s = inp.read()
s = s.replace('\n','')

matches = re.findall(pattern,s)
start_time = time.time()
parsed = parse(matches[0][0],matches[0][1],0)
end_time = time.time()
elapsed_time = end_time-start_time
print(f'Время выполнения: {elapsed_time} секунд')
out.write(parsed)
