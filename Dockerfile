FROM ubuntu:latest

RUN apt-get update && apt-get install -y qt6-base-dev \
  qt6-declarative-dev \
  libqt6svg6-dev \
  optipng
WORKDIR /root/comaps
RUN ./configure.sh
RUN ./tools/unix/generate_symbols.sh
