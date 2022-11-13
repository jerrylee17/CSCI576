from cmath import log
from math import floor, log2
import pprint
from typing import List

pp = pprint.PrettyPrinter(indent=4)

lum = """
188 180 155 149 179 116 86 96
168 179 168 174 180 111 86 95
150 166 175 189 165 101 88 97
163 165 179 184 135 90 91 96
170 180 178 144 102 87 91 98
175 174 141 104 85 83 88 96
153 134 105 82 83 87 92 96
117 104 86 80 86 90 92 103
"""

lum = """
1016  215   -6  -27   29  -20  -11    7
  136   52  -93   -7   34  -18  -11   10
  -45  -49   13   53   11  -24    0    8
    8   38   47   15  -17  -10    4    3
   -1   -5   -1   -4    0    6    4    0
   -4   -1    3    8    7    6    0    1
   -2   -2    0   -1    0   -3    0   -1
    0   -3    0   -1   -4   -1    2    1
"""

quant_table = """
16 11 10 16 124 140 151 161
12 12 14 19 126 158 160 155
14 13 16 24 140 157 169 156
14 17 22 29 151 187 180 162
18 22 37 56 168 109 103 177
24 35 55 64 181 104 113 192
49 64 78 87 103 121 120 101
72 92 95 98 112 100 103 199
"""

run_size_table = """
0/0 14 1010
0/1 12 00
0/2 12 01
0/3 13 100
0/4 14 1011
1/2 15 11011
4/1 16 111011
4/2 10 1111111000
5/1 17 1111010
0/5 15 11010
"""

def parse_run_size_table(s):
    res = {}
    for line in s.split('\n'):
        if not len(line.strip()):
            continue
        run_size, _, code = line.split(' ')
        run, size = run_size.split('/')
        res[(int(run), int(size))] = code
    return res

def lumto2D(lum):
    arr = []
    for row in lum.split('\n'):
        arr.append(list(map(int, row.split())))
    # first and last are blank
    return arr[1:-1]

def lumto1D(lum):
    arr = []
    for a in lum.split():
        arr.append(int(a))
    return arr

def quant(lum, quant_table):
    res = [[0 for i in range(8)] for j in range(8)]
    for i in range(8):
        for j in range(8):
            res[i][j] = round(lum[i][j] / quant_table[i][j])
    return res

def zigzag(matrix: List[List[int]]):
    rows = 8
    columns = 8
    solution=[[] for i in range(rows+columns-1)]
    
    for i in range(rows):
        for j in range(columns):
            sum=i+j
            if(sum%2 ==0):
                #add at beginning
                solution[sum].insert(0,matrix[i][j])
            else:
                #add at end of the list
                solution[sum].append(matrix[i][j])
    
    a = []
    for s in solution:
        a.extend(s)
    return a

def intermediate(zigzag):
    zero_cnt = 0
    res = []
    for x in zigzag[1:]:
        if x == 0:
            zero_cnt += 1
            continue
        else:
            num_bits = floor(log2(abs(x))) + 1
            res.append((zero_cnt, num_bits, x))
            zero_cnt = 0
    return res

def dispIntermediate(intermediate):
    for line in intermediate:
        x, y, z = line
        print(f'<{x}, {y}> <{z}>')

def vli_encode(x):
    if x > 0:
        return bin(x)[2:]
    else:
        res = ''
        b = bin(x)[2:]
        for c in b:
            if c == '0':
                res += '1'
            else:
                res += '0'
    return res

def get_stream(inter_not, run_size_table):
    res = ''
    for a, b, c in inter_not:
        code_word = run_size_table[(a, b)]
        vli = vli_encode(c)
        res += f'{code_word} {vli} '
    return res


a = lumto2D(lum)
# pp.pprint(a)

quant_table = lumto2D(quant_table)
b = quant(a, quant_table)
pp.pprint(b)

c = zigzag(b)
print(c)

d = intermediate(c)
dispIntermediate(d)

e = parse_run_size_table(run_size_table)
pp.pprint(e)

f = get_stream(d, e)
print(f)

