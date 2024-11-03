FROM gitpod/workspace-java-17

# Install additional tools
RUN bash -c ". /home/gitpod/.sdkman/bin/sdkman-init.sh && \
    sdk install maven"

# Install MySQL client
RUN sudo apt-get update && \
    sudo apt-get install -y mysql-client 