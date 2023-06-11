def com_av(filename):
    ts_total = 0
    tj_total = 0
    ts_count = 0
    tj_count = 0

    with open(filename, 'r') as f:
        for line in f.readlines():
            line = line.strip() 
            if line.startswith("TS Time:"):
                ts_time = float(line.split(":")[1])
                ts_total += ts_time
                ts_count += 1
            elif line.startswith("TJ Time:"):
                tj_time = float(line.split(":")[1])
                tj_total += tj_time
                tj_count += 1

    ts_average = ts_total / (1.0*ts_count) 
    tj_average = tj_total / (1.0*tj_count)

    return ts_average, tj_average

# Usage
#change the file name here to correct log file name
ts_avg, tj_avg = com_av("./scaled/1tBalancedPooling/1tBalancedPoolingServer.txt")

print(f"TS Average: {ts_avg}")
print(f"TJ Average: {tj_avg}")
#should be average1.txt here
#every time you run it, you should go into the average1.txt file and label the output to the
#according test!
with open("average1.txt", "a") as f:
    f.write(f'ts_average: {ts_avg} tj_average: {tj_avg}')
    f.close()