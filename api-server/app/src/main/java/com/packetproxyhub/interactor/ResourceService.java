/*
 * Copyright 2021 DeNA Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.packetproxyhub.interactor;

import com.google.inject.Inject;
import com.packetproxyhub.entity.*;

public class ResourceService implements IResourceService {
    @Inject
    private IAccountService accountService;
    @Inject
    private IOrgService orgService;
    @Inject
    private IProjectService projectService;
    @Inject
    private IConfigService configService;
    @Inject
    private IOrgMemberService orgMemberService;
    @Inject
    private IBinaryService binaryService;
    @Inject
    private ISnapshotService snapshotService;

    public Resource getAllResource(Id myAccountId) throws Exception {
        Resource resource = Resource.create();

        Orgs orgs = orgService.listReadableOrgs(myAccountId);
        resource.setOrgs(orgs);

        Accounts accounts = accountService.getAccounts(myAccountId);
        resource.setAccounts(accounts);

        orgs.set().forEach(org -> {
            try {

                Projects projects = projectService.getProjects(myAccountId, org.getId());
                if (!projects.set().isEmpty()) {
                    resource.addProjects(org.getId(), projects);
                }

                for (Project project: projects.set()) {
                    Configs configs = configService.getConfigs(myAccountId, org.getId(), project.getId());
                    if (!configs.set().isEmpty()) {
                        resource.addConfigs(org.getId(), project.getId(), configs);
                        for (Config config: configs.set()) {
                            Binaries binaries = binaryService.getBinaries(myAccountId, org.getId(), project.getId(), config.getId());
                            if (!binaries.set().isEmpty()) {
                                resource.addBinaries(org.getId(), project.getId(), config.getId(), binaries);
                            }
                            Snapshots snapshots = snapshotService.getSnapshots(myAccountId, org.getId(), project.getId(), config.getId());
                            if (!snapshots.set().isEmpty()) {
                                resource.addSnapshots(org.getId(), project.getId(), config.getId(), snapshots);
                            }
                        }
                    }
                }

                OrgMembers orgMembers = orgMemberService.getOrgMembers(myAccountId, org.getId());
                if (!orgMembers.set().isEmpty()) {
                    resource.addOrgMembers(org.getId(), orgMembers);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return resource;
    }

}