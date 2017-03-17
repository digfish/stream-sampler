This little application is a demo that can be used to test the memory, namely how data is copied into the memory from the operation system or using an internal source from Java.

The quick way to invoke the program is through:

`./stream-sampler 5 -i 100`

which will write 100 MegaBytes into the memory and use creates a random sample of 5 bytes from it .


Another way is to pipe some command that outputs data, as in:

`cat somefile.txt | ./stream-sampler 5`

which will read all the data that comes through and samples 5 bytes from it.

A more complex example can be:

`dd if=/dev/urandom count=100 bs=1MB | base64 | ./stream-sampler 5 `

which uses a random generator from the system, codifies data into base64 format and writes into the memory, and again fetches 5 random bytes from the memory.

Available unitary tests can be run with:

`./run-tests.sh`

The tests show the quantity of allocated memory at start, and also the maximum available memory.
At the end of each test, the total allocated memory, and also remaining available are shown.
