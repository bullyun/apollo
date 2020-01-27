#!/bin/sh

docker rmi registry.cn-hangzhou.aliyuncs.com/bullyun/apollo-configservice:1.5.1 -f
docker tag apollo-configservice:1.5.1 registry.cn-hangzhou.aliyuncs.com/bullyun/apollo-configservice:1.5.1
docker push registry.cn-hangzhou.aliyuncs.com/bullyun/apollo-configservice:1.5.1

