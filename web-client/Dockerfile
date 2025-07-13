FROM node:16-bullseye as build-stage

WORKDIR /root
COPY . .
RUN yarn install && yarn build


FROM nginx as production-stage

ENV LANG C.UTF-8
RUN ln -sf /usr/share/zoneinfo/Asia/Tokyo /etc/localtime

COPY nginx/nginx.conf /etc/nginx/nginx.conf
COPY --from=build-stage /root/build /usr/share/nginx/html

EXPOSE 443
ENTRYPOINT ["/usr/sbin/nginx", "-g", "daemon off;", "-c", "/etc/nginx/nginx.conf"]

