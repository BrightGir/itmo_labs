
import re

res = ''
pattern = r'<(\w+)>(.*?)<\/\1>'
opened_tag = 'OPEN_TAG'
closed_tag = 'CLOSED_TAG'
text = 'TEXT'
whitespace = 'WHITESPACE'
structure_attributes_name = 'attributes'

lex = {opened_tag: r'<(\w+)((\s[^"=\s]+="\w+")*)>',
       closed_tag: r'<\/(\w+)>',
       text: r'([^<]+)',
       whitespace: r'(\s+)'}

tags = []

class Tag:

    def __init__(self, name, content: list):
        self.name = name
        self.content = content
        self.extend_list_tags = []

    def __str__(self):
        return f"{self.name}: {self.content}"

    def __repr__(self):
        return self.__str__()

    def copy(self):
        return copy.copy(self)


def regular_exp_element(tag):
    pass


def parse(s: str, pos: int):
    level = 0
    opened_tags = []
    while pos < len(s):
        match = re.match(lex[whitespace], s[pos:])
        if match:
            pos += match.end()
            continue
        match = re.match(lex[opened_tag], s[pos:])
        if match:
            tag = Tag(match.group(1), [])
            attributes = match.group(2)
            if attributes:
                attr_list_tag = Tag(structure_attributes_name, [])
                attributes = attributes[1:]  #убираем первый пробел
                for full_attr in attributes.split(' '):
                    full_attr_splitted = full_attr.split('=')
                    name = full_attr_splitted[0]
                    value = full_attr_splitted[1]
                    value = value[1:len(value) - 1]
                    attr_tag = Tag(name, value)
                    attr_list_tag.content.append(attr_tag)
                tag.content.append(attr_list_tag)
            opened_tags.append(tag)
            pos += match.end()
            continue
        match = re.match(lex[text], s[pos:])
        if match:
            founded_text = match.group(1)
            pos += match.end()
            match = re.match(lex[closed_tag], s[pos:])
            if not match:
                print("Неправильный XML формат")
                return
            pos += match.end()
            tag = Tag(match.group(1), founded_text)
            opened_tags.pop()
            last_tag = opened_tags[-1]
            last_tag.content.append(tag)
            continue
        match = re.match(lex[closed_tag], s[pos:])
        if match:
            cur_tag = opened_tags[-1]
            if len(opened_tags) == 1:
                tags.append(opened_tags[-1])  #добавляем высший
            opened_tags.pop()
            if len(opened_tags) != 0:
                last_tag = opened_tags[-1]
                analogy_name = False
                for last_tag_child in last_tag.content:
                    last_tag_child: Tag = last_tag_child
                    if last_tag_child.name == cur_tag.name:
                        last_tag_child.extend_list_tags.append(cur_tag)
                        analogy_name = True
                        break
                if not analogy_name:
                    last_tag.content.append(cur_tag)
            pos += match.end()
            continue
        pos += 1


def xml_to_yaml(tag: Tag, level: int):
    s = '  ' * level + tag.name + ":" + '\n'
    if not len(tag.extend_list_tags):
        for child in tag.content:
            if (isinstance(child, str)):
                return level * '  ' + tag.name + ": " + tag.content + '\n'
            s += xml_to_yaml(child, level + 1)
        return s
    i = 0
    for child in tag.content:
        child:Tag = child
        s += '  '*(level+1) + ('-' if i == 0 else ' ') + xml_to_yaml(child, level + 1)[2*(level+1)-1:]
        i += 1

    for ex_tag in tag.extend_list_tags:
        i = 0
        for ex_tag_child in ex_tag.content:
            ex_tag_child: Tag = ex_tag_child
            s += ('  ' * (level + 1) + ('-' if i == 0 else ' ') +
                  xml_to_yaml(ex_tag_child, level + 1)[2 * (level + 1) - 1:])
            i += 1
    return s

inp = open('sc.xml', "r", encoding="utf-8")
out = open("schedule.yaml", "w", encoding='utf-8')
s = inp.read()
s = s.replace('\n', '')
parse(s, 0)
out.write(xml_to_yaml(tags[0],0))
