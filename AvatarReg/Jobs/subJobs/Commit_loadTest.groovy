//incoming parameters
//def serviceName = "avreg"
//def gitLoadTestURL = "https://github.com/WPPg2/CTF"
//def gitCredentials = "8cf0000b-3991-4db0-a2d9-e157168d2cef"
//def buildTrigger = "NIGHTLY"


def buildPublishTargetNode = 'Morpheus'
def deployAmiTargetNode = 'AMIBuilder'
//def rootPomPath="LoadTests/${serviceName}"
def parentstageName = "Commit"


stage "${parentstageName}::BuildAndPublish"

    subJob1 = build  job: '../../wppCommon/subJobs/buildAndPublish'/*,
                    parameters: [
                        [$class: 'StringParameterValue', name: 'gitCredentials', value: gitCredentials ],
                        [$class: 'StringParameterValue', name: 'url', value: gitLoadTestURL ],
                        [$class: 'StringParameterValue', name: 'buildTrigger', value: buildTrigger ],
                        [$class: 'StringParameterValue', name: 'serviceName', value: serviceName ],
                        [$class: 'StringParameterValue', name: 'targetNode', value: buildPublishTargetNode ],
                        [$class: 'StringParameterValue', name: 'rootPomPath', value: rootPomPath ],
                    ] ;
    
    // Return Values
    //def artifactURL = subJob1.description.tokenize('#')[0].tokenize('=')[1]
    //def artifactVersion = subJob1.description.tokenize('#')[1].tokenize('=')[1]
    //def commitID = subJob1.description.tokenize('#')[2].tokenize('=')[1]
    
currentBuild.setDescription("#artifactURL="+artifactURL+"#artifactVersion="+artifactVersion+"#commitID="+commitID)*/
    
