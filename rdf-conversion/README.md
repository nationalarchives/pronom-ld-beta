PRONOM RDF conversion
===

This repo holds the R2RML scripts used to convert the existing PRONOM data to RDF.

It requires

Each table is converted by a different mapping file. The naming convention is [table-name].ttl for mapping files. eg: actors.ttl is the mapping file for the actors table.

Before any conversion can be run, an implementation of an r2rml engine must be downloaded. The one used for the example commands is R2RML-F, which can be found [here](https://github.com/chrdebru/r2rml-distributions)

The following command can be used to convert the data. It must point to a running instance of the pronom database:

```bash
java -jar r2rml-f/r2rml.jar -m r2rml/actors.ttl -o out/actors.ttl -c jdbc:sqlserver://some.host.net:10443;user=$USER; -p $PASSWORD
```

## Batch conversion

There is a script which will essentially run the command specified above for each file in the r2rml directory.

All the variables are hardcoded to point to Wallscope's development server except for the password, which must be passed into the script executions, like so:

```bash
PASSWORD=ThisIsASafePW ./batch.sh
```
