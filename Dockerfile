FROM maven:3-alpine

RUN sudo apt install node -y
RUN sudo apt install npm -y