import { Component, OnInit } from '@angular/core';
import { Channel } from '../entities/channel/channel.model';
import { Factor } from '../entities/factor/factor.model';
import { Style } from '../entities/style/style.model';
import { Types } from '../entities/types/types.model';
import { TransactionDetails } from '../entities/transaction-details/transaction-details.model';
import { Transactions } from '../entities/transactions/transactions.model';
import { TransactionsService } from '../entities/transactions/transactions.service';
import { ChannelService } from '../entities/channel/channel.service';
import { FactorService } from '../entities/factor/factor.service';
import { StyleService } from '../entities/style/style.service';
import { TypesService } from '../entities/types/types.service';
import { AlertService, EventManager } from 'ng-jhipster';
import { Response } from '@angular/http';
import { DatePipe } from '@angular/common';
import { Observable } from 'rxjs/Observable';
import { StorageService } from '../shared/storage/storage.service';
/**
 * Created by Dai Mai on 6/17/17.
 */
@Component({
    selector: 'jhi-tool',
    templateUrl: './tool.component.html',
    styleUrls: [
        'tool.component.scss'
    ]
})
export class QuanLySoToolComponent implements OnInit {
    channels: Channel[];
    factors: Factor[];
    styles: Style[];
    types: Types[];
    transactions: Transactions;
    isProcess: boolean;
    private DATE_FORMAT = 'yyyy-MM-ddT00:00';

    constructor(private eventManager: EventManager,
                private transactionsService: TransactionsService,
                private channelService: ChannelService,
                private factorService: FactorService,
                private styleService: StyleService,
                private typesService: TypesService,
                private alertService: AlertService,
                private datePipe: DatePipe,
                private storageService: StorageService
    ) {
        this.reset();
    }

    ngOnInit(): void {
        this.transactions.openDate = this.datePipe.transform(Date.now(), this.DATE_FORMAT);
        this.onOpenDateChange();
    }

    onOpenDateChange(): void {
        const openDate: string = this.datePipe.transform(this.transactions.openDate, 'EEEE').toLowerCase();
        this.init(openDate);
    }

    addRecord(): void {
        this.transactions.transactionDetailsDTOs.push(new TransactionDetails());
    }

    removeRecord(target: TransactionDetails): void {
        const idx: number = this.transactions.transactionDetailsDTOs.findIndex((t: TransactionDetails) => t === target);
        if (idx > -1) {
            this.transactions.transactionDetailsDTOs.splice(idx, 1);
        }
    }

    reset(): void {
        this.transactions = new Transactions();
        this.transactions.clientsId = this.storageService.getAccountId();
        this.transactions.transactionDetailsDTOs = [];
        this.addRecord();
    }

    check(): void {
        this.isProcess = true;
        this.subscribeToSaveResponse(
            this.transactionsService.create(this.transactions));
    }

    private init(openDate: string): void {
        this.channelService.findByOpenDay(openDate).subscribe(
            (res: Response) => { this.channels = res.json(); }, (res: Response) => this.onError(res.json()));
        this.factorService.query().subscribe(
            (res: Response) => { this.factors = res.json(); }, (res: Response) => this.onError(res.json()));
        this.styleService.query().subscribe(
            (res: Response) => { this.styles = res.json(); }, (res: Response) => this.onError(res.json()));
        this.typesService.query().subscribe(
            (res: Response) => { this.types = res.json(); }, (res: Response) => this.onError(res.json()));
    }

    private subscribeToSaveResponse(result: Observable<Channel>) {
        result.subscribe((res: Channel) =>
            this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
    }

    private onSaveSuccess(result: Channel) {
        this.eventManager.broadcast({ name: 'channelListModification', content: 'OK'});
        this.isProcess = false;
    }

    private onSaveError(error) {
        try {
            error.json();
        } catch (exception) {
            error.message = error.text();
        }
        this.isProcess = false;
        this.onError(error);
    }

    private onError(error) {
        this.alertService.error(error.message, null, null);
    }

}
