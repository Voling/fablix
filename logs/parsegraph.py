import re
def com_av(filename):
    total = 0
    counter = 0
    with open(filename, 'r') as f:
        
        for line in f.readlines():
            line = line.strip() 
            print(line)
            elapsed_times = re.findall(r",(\d+),add", line)
            if elapsed_times != []:            
                total += float(elapsed_times[0])
                print(elapsed_times)
            counter += 1

        # Convert strings to integers.

        # Compute the average.
        average = total / counter

      
        return average

# Usage
#change the file name here to correct log file name
avg = com_av("./scaled/1tBalancedPooling/1tBalancedPoolingGraph.txt")

print(f"Average:{avg}")
#should be average1.txt here
#every time you run it, you should go into the average1.txt file and label the output to the
#according test!
with open("averagegraph.txt", "a") as f:
    f.write(f'average{avg}')
    f.close()