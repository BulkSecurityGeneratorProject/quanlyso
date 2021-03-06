import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { PaginationUtil } from 'ng-jhipster';

import { CodeComponent } from './code.component';
import { CodeDetailComponent } from './code-detail.component';
import { CodePopupComponent } from './code-dialog.component';
import { CodeDeletePopupComponent } from './code-delete-dialog.component';

import { Principal } from '../../shared';

@Injectable()
export class CodeResolvePagingParams implements Resolve<any> {

    constructor(private paginationUtil: PaginationUtil) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const page = route.queryParams['page'] ? route.queryParams['page'] : '1';
        const sort = route.queryParams['sort'] ? route.queryParams['sort'] : 'id,asc';
        return {
            page: this.paginationUtil.parsePage(page),
            predicate: this.paginationUtil.parsePredicate(sort),
            ascending: this.paginationUtil.parseAscending(sort)
      };
    }
}

export const codeRoute: Routes = [
    {
        path: 'code',
        component: CodeComponent,
        resolve: {
            'pagingParams': CodeResolvePagingParams
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'quanlysoApp.code.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'code/:id',
        component: CodeDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'quanlysoApp.code.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const codePopupRoute: Routes = [
    {
        path: 'code-new',
        component: CodePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'quanlysoApp.code.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'code/:id/edit',
        component: CodePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'quanlysoApp.code.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'code/:id/delete',
        component: CodeDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'quanlysoApp.code.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
