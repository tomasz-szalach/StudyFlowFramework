FROM postgres:latest

ENV POSTGRES_PASSWORD=docker
ENV POSTGRES_DB=db
ENV POSTGRES_USER=docker

COPY ./docker/db/extension-uuid.sql /docker-entrypoint-initdb.d/
