pipeline {
    agent none
    environment {
        VARS_FILE='./vars'
        SECRETS_KEYCLOAK='Keycloak-Server/Keys'
        CF_TEMPLATE_PATH='cloudformation/Keycloak-Server.yaml'
        HOME = "${WORKSPACE}"
        NPM_CONFIG_CACHE = "${WORKSPACE}/.npm"
    }
    stages {
        stage('Init') {
            steps {
                echo 'Init'
            }
        }
        stage('Build') {
           agent {
                dockerfile{
                    filename 'Dockerfile'
                    label 'ucms-docker-agent'
                }   
            }
            steps {
                sh '''#!/bin/bash
                    echo "Building Keycloak"
                    mvn -Pdistribution -pl distribution/server-dist -am -Dmaven.test.skip clean install
                '''
                stash includes: 'distribution/server-dist/target/keycloak-12.0.0-SNAPSHOT.tar.gz', name: 'server'
                stash includes: 'modules/standalone.xml', name: 'config'
                stash includes: 'modules/postgresql/main/*', name: 'postgresql'
            }
        }
        stage('Testing') {
            steps {
                echo "Tests removed until we have a simplified test suite"
            }
        }
        stage('Deploy') {
            agent {
                label 'aws-serverless'
            }
            steps {
                script {
                    echo "Deploying Keycloak"
                    withAWS(credentials:'AJPlus Systems Access') {
                        sshagent (credentials:["6ee01661-f84d-41fe-880b-05d047312c3c"]) {
                            unstash 'server'
                            unstash 'config'
                            unstash 'postgresql'
                            sh '''#!/bin/bash
                                echo "#!/bin/bash" > $VARS_FILE
                                #secrets are only pulled from one region of an account so using --region arg in below
                                aws --region eu-west-1 --output json secretsmanager get-secret-value --secret-id $SECRETS_KEYCLOAK | jq -r '.SecretString' | jq -r 'to_entries|map(.key+"="+.value|tostring)|.[]' >> $VARS_FILE
                                chmod +x $VARS_FILE
                                . $VARS_FILE

                                CF_TEMPLATE_PATH=`echo "${CF_TEMPLATE_PATH}" | sed -e 's/^[ \t]*//'`
                                stack_name=`basename ${CF_TEMPLATE_PATH} | cut -d'.' -f1`
                                aws cloudformation deploy \
                                --no-fail-on-empty-changeset \
                                --template-file ${CF_TEMPLATE_PATH} \
                                --stack-name ${stack_name} --region eu-west-1 \
                                --tags  Name="${stack_name} CF Stack" \
                                        "Business / Service Owner"=Digital-DevOps@aljazeera.net \
                                        Purpose=Network \
                                        ProductID=${stack_name} \
                                        Environment=Shared \
                                        CreatedBy=rosab@aljazeera.net \
                                --parameter-overrides PagerDutyKey=${PagerDutyKey} \
                                        DbUsername=${DbUsername} \
                                        DbInstance=${DbInstance} \
                                        DbPassword=${DbPassword} \
                                        AdminUsername=${AdminUsername} \
                                        AdminPassword=${AdminPassword} \
                                        KeyName=${KeyName} \
                                        EnvironmentName=Shared \
                                        ProductName=Keycloak \
                                        InstanceType=t2.small \
                                        CreatorEmail=rosab@aljazeera.net \
                                        TeamEmail=Digital-Devops@aljazeera.net \
                                        NewRelicKey=${NewRelicKey} \
                                        InstanceSecurityGroup=${InstanceSecurityGroup} \

                                ls
                                export INSTANCE_ADDRESS=`aws --region eu-west-1 ec2 describe-instances --filters "Name=tag:Name,Values=Keycloak-Shared" \
                                --query "Reservations[*].Instances[*].PublicIpAddress" \
                                --output=text`
                                echo $INSTANCE_ADDRESS     
                                ssh -o StrictHostKeyChecking=no ubuntu@$INSTANCE_ADDRESS uptime
                                ssh -v ubuntu@$INSTANCE_ADDRESS
                                scp distribution/server-dist/target/keycloak-12.0.0-SNAPSHOT.tar.gz StrictHostKeyChecking=no ubuntu@$INSTANCE_ADDRESS:/home/ubuntu
                                scp modules/standalone.xml StrictHostKeyChecking=no ubuntu@$INSTANCE_ADDRESS:/home/ubuntu
                                scp -r modules/postgresql StrictHostKeyChecking=no ubuntu@$INSTANCE_ADDRESS:/home/ubuntu
                            '''                        
                        }
                    }
                }
            }
        }
    }
}