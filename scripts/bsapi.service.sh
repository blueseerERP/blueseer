#!/bin/bash
/opt/blueseer/jre17/bin/java -cp "custom/*:dist/*" utilities.apiServer -debug -ssl -port 8099 -config bs.cfg