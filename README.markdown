pq
==

`pq` is a command line tool for editing Java Properties files.

## Motivation
Using tools like `sed` or `awk` when dealing with properties can sometimes lead to creating
a file that isn't able to be correctly loaded by the Java Properties class, this tool aims
to fix that by making it easy to edit Java Properties files on the command line.

## Requirements
`pq`'s only system dependency is a Java JRE >= 1.7

## Usage
```
Usage: pq [--file <in>|-] ["key=value" | [ "key2=value2"]]
       -h | --help        Print this help
       -f | --file        Specify the properties files to read
       -                  Read properties from stdin
       --version          Prints version and jvm info
```

### Reading properties
Properties can be read either from a specified file using `-f / --file` or from `stdin` by passing `-`.
The priority used to determine the input source is as follows:

1. if `-` is passed `stdin` will be used
2. if no `-` then the first `-f / --file` arg will be used
3. if no input can be detected the app will exit

### Applying new Properties
After the input is determined and loaded, all provided properties will be applied in order
on top of what was loaded.

### Output
The resulting composite of the input and provided properties will then be written to stdout.

## Current limitations
Right now `pq` only allow outputting to stdout. This means that in-place editing of properties
files is currently not possible. This is intentional so that no data is ever unknowingly destroyed.
(See future features for an idea of why this is the case)

Simply redirecting the output of `pq` to the original file will not help as opening a file for
writing destroys the contents of the file. For example, the following will not work:
```bash
$ pq --file server.properties "port=8081" > server.properties
```

Instead you should first move the original file like this:
```bash
$ mv server.properties server.properties.orig
$ pq --file server.properties.orig "port=8081" > server.properties
```

## Future features
* Syntax highlighting of properties files in the terminal
* Maintain comments from original file
* Maintain order of properties from original file
* Add the ability to query a properties file.

## Inspiration
* [jq](http://stedolan.github.io/jq/)

