RDF triplestore Docker image build
===

This docker image is built from secoresearch/fuseki. It loads the initial data into the triplestore and runs optimisation and text indexing.

## Building the image

In order to avoid copying the data files into the Docker build context, the docker build command should be run from the parent directory which holds the results of the rdf-conversion process.

```bash
cd ${REPO_ROOT}/rdf-conversion
docker build -f triplestore/Dockerfile -t ${AWS_ACCOUNT_ID}.dkr.ecr.eu-west-2.amazonaws.com/pronom-triplestore:${VERSION} .
```

## Pushing to ECR repository

In order to push this image to the ECR repository, the steps to the deploy the Terraform ECR configuration must be followed beforehand.
