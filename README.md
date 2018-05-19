# EvoGraph
EvoGraph: An Effective and Efficient Graph Upscaling Method for Preserving Graph Properties

## Usage (Simple example)
 - Git
``
git clone https://github.com/chan150/EvoGraph
`` 

 - Compile 
``
bash compile.sh
``
or 
``
bash compile-local.sh
``

 - Execute TrillionG in single machine
``
bash run-local.sh output_file
``

 - Execute TrillionG in distributed machines
``
bash run.sh output_file
``

## Usage (Detail example)
 - generate scale20 graph which has 0.1 noises as tsv output format format by using 120 machines/threads 
``
bash run.sh output -n 20 -m 120 -format tsv -noise 0.1
``


## Parameters
 - **-p** seed parameter (e.g. -p 0.57,0.19,0.19,0.05)
 - **-logn** or **-n** scale of graph (number of vertices in log-scale) (e.g. -n 20)
 - **-ratio** or **-r** ratio between number of vertices and number of edges (e.g. -r 16)
 - **-noise** adding noises for more realistic degree distribution (e.g. -noise 0.1) 
 - **-machine** or **-m** number of machines/threads (e.g. -m 120)
 - **-format** output format** (e.g. -format tsv)
 - **-compress** compression codec (e.g. -compress snappy; Snappy must be set in Hadoop)

## Output Format
 - **tsv** Edge list format with tap sperated value 
 - **adj** Adjacency list format with 6 byte alignment, in addition, **adj4**, **adj6**, **adj8**
 - **csr** Compresed sparse row format with 6 byte alignment, in addition, **csr4**, **csr6**, **csr8**
 - or explicitly write the class name

## Compression codec
 - **snappy** Snappy compression codec
 - **bzip** or **bzip2** Bzip2 compression codec
 - or explicitly write the class name
 
## License
Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

## Citing the paper

```
@inproceedings{Park:2017:TTS:3035918.3064014,
 author = {Park, Himchan and Kim, Min-Soo},
 title = {TrillionG: A Trillion-scale Synthetic Graph Generator Using a Recursive Vector Model},
 booktitle = {Proceedings of the 2017 ACM International Conference on Management of Data},
 series = {SIGMOD '17},
 year = {2017},
 isbn = {978-1-4503-4197-4},
 location = {Chicago, Illinois, USA},
 pages = {913--928},
 numpages = {16},
 url = {http://doi.acm.org/10.1145/3035918.3064014},
 doi = {10.1145/3035918.3064014},
 acmid = {3064014},
 publisher = {ACM},
 address = {New York, NY, USA},
 keywords = {benchmark, distributed, graph generation, ldbc, rdf, realistic, recursive vector model, rmat, scalability, synthetic},
} 
```
