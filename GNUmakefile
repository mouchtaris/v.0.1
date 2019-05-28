TSC_OUT = ./dist
TSC_SRC = ./src
TSC_OPTS = $(shell bin/tsc_flags)

TSC = tsc
MKDIR = mkdir

${TSC_OUT}/%.js: ${TSC_SRC}/%.ts
	${MKDIR} -pv $$(dirname $@)
	${TSC} ${TSC_OPTS} <$< >$@

all: \
	${TSC_OUT}/cats/index.js \
	${TSC_OUT}/server.js \
	${TSC_OUT}/index.js \

clean:
	rm -rfv ${TSC_OUT}

server: ${TSC_OUT}/server.js
	node --experimental-modules $<

${TSC_OUT}:
	mkdir -pv $@

.PHONY: all clean server