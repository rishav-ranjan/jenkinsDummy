//incoming parameters - serviceName, amiID, serviceConfigBaseURL

def parentstageName = "Capacity"
def deployAmiTargetNode
def gitCredentials
def gitDeployURL
def loadTestTargetNode
node('master') {
	def masterWorkspace = pwd()
	sh """mkdir -p "${masterWorkspace}"
	"""
	getConfigFile(serviceConfigBaseURL,"serviceConfig.groovy")
	serviceConfigFile = new File("${masterWorkspace}/serviceConfig.groovy")
	def configObject = new ConfigSlurper().parse(serviceConfigFile.text)
	loadTestTargetNode=configObject.loadTestTargetNode
	deployAmiTargetNode=configObject.deployAmiTargetNode
	gitDeployURL=configObject.gitDeployURL
	gitCredentials=configObject.gitCredentials
}

def subnetNums=2
for (subnetNum=1;subnetNum<=subnetNums;subnetNum++) {
	stage "${parentstageName}::DeploySubnet ${subnetNum}"
	subJob = build  job: '../../wppCommon/subJobs/subnetDeploy',
            parameters: [
						[$class: 'StringParameterValue', name: 'gitCredentials', value: gitCredentials ],
				        [$class: 'StringParameterValue', name: 'gitDeployURL', value: gitDeployURL ],
						[$class: 'StringParameterValue', name: 'serviceName', value: serviceName ],
						[$class: 'StringParameterValue', name: 'amiID', value: amiID ],
						[$class: 'StringParameterValue', name: 'subnetNum', value: subnetNum.toString() ],
				        [$class: 'StringParameterValue', name: 'serviceConfigBaseURL', value: serviceConfigBaseURL],
				        [$class: 'StringParameterValue', name: 'targetNode', value: deployAmiTargetNode ],
            ];
}

stage "${parentstageName}::runMicroLoadTest"
/*subJob = build  job: '../../wppCommon/subJobs/runMicroLoadTest',
            parameters: [
				[$class: 'StringParameterValue', name: 'serviceConfigBaseURL', value: serviceConfigBaseURL ],
				[$class: 'StringParameterValue', name: 'loadTestTargetNode', value: loadTestTargetNode ],
                ];
*/
sleep(10)
				
def getConfigFile(baseURL,fileName) {
    def workspace = pwd()
    def file = new File("${workspace}/${fileName}").newOutputStream()  
    file << new URL("${baseURL}/${fileName}").openStream()  
    file.close()
}