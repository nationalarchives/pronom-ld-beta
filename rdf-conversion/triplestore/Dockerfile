FROM secoresearch/fuseki:4.6.1

COPY ${PWD}/triplestore/assembler.ttl /fuseki-base/configuration/assembler.ttl
COPY --chown=9008 ${PWD}/out /tmp/load

RUN $TDBLOADER --graph=http://www.nationalarchives.gov.uk/PRONOM/ /tmp/load/* \
	&& $TEXTINDEXER \
	&& $TDBSTATS --graph urn:x-arq:UnionGraph > /tmp/stats.opt \
	&& mv /tmp/stats.opt /fuseki-base/databases/tdb/ \
	&& rm -r /tmp/load