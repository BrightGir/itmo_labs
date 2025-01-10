import time

inp = open('schedule.xml', "r", encoding="utf-8")
out = open("schedule.yaml", "w", encoding='utf-8')
level = 0
start_time = time.time()
s = ""
for tag in inp:
    if tag.count('<') == 1:
        if tag.count('/') != 0:
            level -= 1
            continue
        name = ''
        i = 0
        while(tag[i] != '<'):
            i += 1
        i += 1
        while (tag[i] != '>'):
            name += tag[i]
            i += 1
        s += '  '*level + name + ": \n"
        level += 1
    else:
        name = ''
        value = ''
        i = 0
        while (tag[i] != '<'):
            i += 1
        i += 1
        while (tag[i] != '>'):
            name += tag[i]
            i += 1
        i += 1
        while (tag[i] != '<'):
            value += tag[i]
            i += 1
        s += '  '*level + name + ": " + value + "\n"

out.write(s)
end_time = time.time()
elapsed_time = end_time - start_time
elapsed_time:float = elapsed_time
print(f"Время выполнения: {elapsed_time} секунд")