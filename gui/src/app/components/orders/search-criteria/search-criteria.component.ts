import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {SearchCriteria} from "../types/SearchCriteria";
import {Stage} from "../types/Stage";
import {StageService} from "../stage.service";

@Component({
    selector: 'search-criteria',
    templateUrl: './search-criteria.component.html'
})
export class SearchCriteriaComponent implements OnInit {

    @Output() searchClicked = new EventEmitter<SearchCriteria>();
    @Output() fetchClicked = new EventEmitter();
    completed: boolean;
    packed: boolean;
    orderName: string;
    stage: string;
    customer: string;
    route: string;
    stages: Stage[];
    processingDate: string;
    orderedAt: string;
    today: Date;

    constructor(private stageProviderService: StageService) {
        this.stages = stageProviderService.getStages();
        this.today = new Date();
        this.today.setHours(this.today.getHours() + 1)
    }

    ngOnInit() {
    }

    search() {
        const searchParams: SearchCriteria = {
            name: this.orderName,
            completed: this.completed,
            stage: this.stage,
            customer: this.customer,
            route: this.route,
            processingDate: this.processingDate,
            orderedAt: this.orderedAt,
            packed: this.packed
        };
        this.searchClicked.emit(searchParams);
    }

    fetch() {
        this.fetchClicked.emit();
    }
}
