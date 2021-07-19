FROM node as build-stage

WORKDIR /root
COPY . .
RUN yarn install && yarn build


FROM nginx as production-stage

ENV LANG C.UTF-8
RUN ln -sf /usr/share/zoneinfo/Asia/Tokyo /etc/localtime

COPY sample/nginx.conf /etc/nginx/nginx.conf
RUN mkdir -p /etc/nginx/certs
COPY path/to/server.key /etc/nginx/certs
COPY path/to/server.key.password /etc/nginx/certs
COPY path/to/server_and_ca.crt /etc/nginx/certs
COPY --from=build-stage /root/build /usr/share/nginx/html

EXPOSE 80 443
ENTRYPOINT ["/usr/sbin/nginx", "-g", "daemon off;", "-c", "/etc/nginx/nginx.conf"]

