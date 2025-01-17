# 基础镜像
FROM mysql:5.7
# author
MAINTAINER ruoyi

# 执行sql脚本
COPY ./db/*.sql /docker-entrypoint-initdb.d/
