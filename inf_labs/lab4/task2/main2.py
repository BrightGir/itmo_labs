inp = open('schedule.xml', "r", encoding="utf-8")
out = open("schedule.yaml", "w", encoding='utf-8')

import xml.etree.ElementTree
import yaml
import time

tree = xml.etree.ElementTree.parse('schedule.xml')
root = tree.getroot()
def parse_xml(el):
    res = {}
    for d in el:
        if len(d) != 0:  # Если есть вложенные элементы
            res[d.tag] = parse_xml(d)
        else:
            res[d.tag] = d.text

    return res


start_time = time.time()
yaml_res = yaml.dump({root.tag: parse_xml(root)}, allow_unicode=True)
end_time = time.time()
elapsed_time = end_time - start_time
elapsed_time:float = elapsed_time
out.write(yaml_res)
print(f"Время выполнения: {elapsed_time} секунд")
