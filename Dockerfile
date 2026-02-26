FROM ubuntu:latest
LABEL authors="heosb"

ENTRYPOINT ["top", "-b"]