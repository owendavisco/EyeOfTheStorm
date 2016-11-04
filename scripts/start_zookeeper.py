import os
import paramiko
import re

def createDataDir(sshClient, serverId):
    stdin, result, err = sshClient.exec_command('find /tmp -maxdepth 1 -type d -name "zookeeper"')
    if not result:
        sshClient.exec_command('mkdir /tmp/zookeeper')
    stdin, result, err = sshClient.exec_command('cat /tmp/zookeeper/myid')
    if not result:
        sshClient.exec_command('echo "{0}" > /tmp/zookeeper/myid'.format(serverId))


def startServer(sshClient, zookeeperHome):
    stdin, processId, err = sshClient.exec_command('nohup {0} > /dev/null 2>&1 & echo $!'.
                                          format(os.path.join(zookeeperHome, 'bin', 'zkServer.sh start')))
    return str(processId.read())


def killServer(sshClient, processId):
    print 'Killing Server with pid:', processId
    stdin, stdout, err = sshClient.exec_command('kill {0}'.format(processId))


def main():
    print 'Configuring Servers...'
    servers = {}
    zookeeperHome = os.environ['ZOOKEEPER_HOME']
    configFilePath = os.path.join(zookeeperHome, 'conf', 'zoo.cfg')
    with open(configFilePath) as configFile:
        for line in configFile:
            serverInfo = re.search('server.([0-9]+)=(.*):[0-9]+:[0-9]+', line)
            if serverInfo:
                serverId = serverInfo.group(1)
                serverHostname = serverInfo.group(2)
                sshClient = paramiko.SSHClient()
                sshClient.set_missing_host_key_policy(paramiko.AutoAddPolicy())
                sshClient.connect(serverHostname)

                createDataDir(sshClient, serverId)
                print '\nStarting Server with Hostname:{0} and ID:{1}'.format(serverHostname, serverId)
                processId = startServer(sshClient, zookeeperHome)
                if processId:
                    print 'Server started successfully with pid:', processId
                    servers[serverId] = {
                        'hostname': serverHostname,
                        'sshClient': sshClient,
                        'processId': processId
                    }
                else:
                    print 'Server failed to start...'
    print '\nConfiguration Completed!'

    # There is currently an issue with pids, the following code is unable to kill zookeeper
    # raw_input('Press Any Key to Stop...')
    # for serverId in servers:
    #     killServer(servers[serverId]['sshClient'], servers[serverId]['processId'])

if __name__ == "__main__":
    main()
