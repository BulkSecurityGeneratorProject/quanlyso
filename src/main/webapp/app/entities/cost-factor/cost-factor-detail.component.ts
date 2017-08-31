import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { AlertService, EventManager } from 'ng-jhipster';

import { CostFactor } from './cost-factor.model';
import { CostFactorService } from './cost-factor.service';
import { StyleService } from '../style/style.service';
import { Style } from '../style/style.model';

@Component({
    selector: 'jhi-cost-factor-detail',
    templateUrl: './cost-factor-detail.component.html'
})
export class CostFactorDetailComponent implements OnInit, OnDestroy {

    costFactor: CostFactor;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: EventManager,
        private costFactorService: CostFactorService,
        private alertService: AlertService,
        private styleService: StyleService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInCostFactors();
    }

    load(id) {
        this.costFactorService.find(id).subscribe((costFactor) => {
            this.costFactor = costFactor;
            this.getStyleName(this.costFactor);
        });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInCostFactors() {
        this.eventSubscriber = this.eventManager.subscribe(
            'costFactorListModification',
            (response) => this.load(this.costFactor.id)
        );
    }

    private onError(error) {
        this.alertService.error(error.message, null, null);
    }

    private getStyleName(costFactor: CostFactor): void {
        this.styleService.find(costFactor.stylesId).toPromise()
            .then((style: Style) => costFactor.stylesName = style.name)
            .catch((error: any) => {
                console.error('Cannot retrieve styles', error);
                this.onError(error);
            });
    }

}
