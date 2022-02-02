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
package com.packetproxyhub.controller.route;

import com.google.inject.Inject;
import com.packetproxyhub.entity.Account;
import com.packetproxyhub.entity.Id;
import com.packetproxyhub.entity.Ids;
import com.packetproxyhub.interactor.IAccountService;
import org.apache.commons.io.IOUtils;
import org.apache.ibatis.exceptions.PersistenceException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

// Accounts Endpoint
// POST   /account/ (create a new account)
// GET    /accounts/123 (get the account)
// GET    /accounts/me
// PUT    /accounts/123 (edit the account)
// PUT    /accounts/me
// DELETE /accounts/123 (delete the account)
// DELETE /accounts/me

public class AccountRoute extends Route {
    @Inject
    private IAccountService accountService;

    // get the account
    @Override
    public boolean getIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        if (match ("/accounts", path, (a) -> {
            try {
                Account myAccount = getAccount(request);
                Ids accountIds = accountService.listAccounts();

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, accountIds.toJson());

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                e.printStackTrace();
            }
        })) { return true; }
        if (match ("/accounts/{accountId}", path, (a) -> {
            try {
                Account myAccount = getAccount(request);
                if (myAccount == null) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                Id accountId = a.get("accountId").equals("me") ? myAccount.getId() : Id.createFromString(a.get("accountId"));
                Account account = accountService.getAccount(myAccount.getId(), accountId);

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, account.toJson());

            } catch (IllegalAccessException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } catch (PersistenceException e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                e.printStackTrace();
            }
        })) { return true; }

        return false;
    }

    // create a new account
    @Override
    public boolean postIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        if (match ("/accounts/", path, (a) -> {
            try {
                Account account = Account.createFromJson(IOUtils.toString(request.getReader()));
                Id accountId = accountService.createAccount(account);

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, accountId.toJson());

            } catch (PersistenceException e) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                e.printStackTrace();
            }
        })) { return true; }

        return false;
    }

    // update the account
    @Override
    public boolean putIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        if (match ("/accounts/{accountId}", path, (a) -> {
            try {
                Account myAccount = getAccount(request);
                Id accountId = a.get("accountId").equals("me") ? myAccount.getId() : Id.createFromString(a.get("accountId"));
                Account account = Account.createFromJson(IOUtils.toString(request.getReader()));

                accountService.updateAccount(myAccount.getId(), account);

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, "{\"status\":\"ok\"}");

            } catch (IllegalAccessException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } catch (PersistenceException e) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                e.printStackTrace();
            }
        })) { return true; }

        return false;
    }
}
