# EvoGraph [SIGKDD18 (to appear)]
EvoGraph: An Effective and Efficient Graph Upscaling Method for Preserving Graph Properties


## Usage (Simple example)
 - Git
``
git clone https://github.com/chan150/EvoGraph.git
`` 

 - Compile 
``
./compile.sh
``
or 
``
./compile-local.sh
``

 - Execute EvoGraph in single machine
``
./run-local.sh output_file
``

 - Execute EvoGraph in distributed machines
``
./run.sh output_file
``

## Usage (Detail example)
 - upscaling a toy graph with a scale factor 2 by using 120 machines/threads 
``
./run.sh output graph -gus.input toy -gus.sf 2 -m 120
``


## Parameters
 - **-gus.input** input path in master computer
 - **-gus.sf** scale factor
 - **-machine** or **-m** number of machines/threads (e.g. -m 120)
 
## License
Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

## Citing the paper
For more details about EvoGraph, please refer to [our paper](http://infolab.dgist.ac.kr/~mskim/papers/SIGKDD18.pdf).

If you use EvoGraph, please cite:
```

```
