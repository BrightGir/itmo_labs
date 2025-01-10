import re

def f(s):
    s1 = s[0]^s[2]^s[4]^s[6]
    s2 = s[1]^s[2]^s[5]^s[6]
    s3 = s[3]^s[4]^s[5]^s[6]
    s = str(s3)+str(s2)+str(s1)
    bit_number = int(s,2)
    return bit_number

out = open("result.txt", "w", encoding='utf-8') 
s = [int(x) for x in input()]
bit_dict = {1: 'r1', 2: 'r2', 3: 'i1', 4: 'r3', 5: 'i2', 6: 'i3', 7: 'i4'}
b = f(s)
if(b == 0):
    out.write('correct')
else:
    out.write(bit_dict[b])