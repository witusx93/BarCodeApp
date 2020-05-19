import {NgModule} from "@angular/core";
import {
    MatButtonModule,
    MatCheckboxModule,
    MatExpansionModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatTableModule
} from "@angular/material";
import {MAT_MOMENT_DATE_ADAPTER_OPTIONS} from "@angular/material-moment-adapter";
import {StageService} from "../orders/stage.service";
import {RoutesComponent} from "./routes.component";
import {CommonModule} from "@angular/common";
import {DragDropModule} from "@angular/cdk/drag-drop";
import {RestService} from "../../services/rest.service";
import {FormsModule} from "@angular/forms";
import {MapDialog} from "./compute-route/map-dialog/map.dialog";
import {AgmCoreModule} from "@agm/core";
import {GoogleApi} from "./compute-route/GoogleApi";
import {ComputeRoute} from './compute-route/compute-route.component';

@NgModule({
    declarations: [
        RoutesComponent,
        MapDialog,
    ],
    imports: [
        MatTableModule,
        MatCheckboxModule,
        MatIconModule,
        MatButtonModule,
        MatFormFieldModule,
        MatInputModule,
        CommonModule,
        DragDropModule,
        MatExpansionModule,
        FormsModule,
        AgmCoreModule.forRoot({
            apiKey: '',
            region: 'pl',
            libraries: ['places', 'drawing', 'geometry','geocoder']
        })
    ],
    entryComponents: [MapDialog],
    providers: [
        StageService,
        RestService,
        GoogleApi,
        ComputeRoute,
        {provide: MAT_MOMENT_DATE_ADAPTER_OPTIONS, useValue: {useUtc: true}}
    ]
})
export class RoutesModule {
}
