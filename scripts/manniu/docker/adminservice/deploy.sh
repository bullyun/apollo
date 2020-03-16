#!/bin/sh

docker rmi registry.cn-hangzhou.aliyuncs.com/bullyun/apollo-adminservice:1.5.1.11-SNAPSHOT -f
docker tag apollo-adminservice:1.5.1.11-SNAPSHOT registry.cn-hangzhou.aliyuncs.com/bullyun/apollo-adminservice:1.5.1.11-SNAPSHOT
docker push registry.cn-hangzhou.aliyuncs.com/bullyun/apollo-adminservice:1.5.1.11-SNAPSHOT

