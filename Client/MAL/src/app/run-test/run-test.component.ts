import { Component, OnInit } from '@angular/core';
import { PayersService } from '../services/payers.service';

@Component({
  selector: 'app-run-test',
  templateUrl: './run-test.component.html',
  styleUrls: ['./run-test.component.scss']
})
export class RunTestComponent implements OnInit {
  displayedColumns: string[] = ['payerName', 'serverUrl','membersInformation','extractInformation','actions'];  
  payersList:any = [];
  groupId:string;

  constructor(private _payers: PayersService) { }

  ngOnInit() {    
    if (localStorage.getItem('payerInfo') !== null) {
      this.payersList = JSON.parse(localStorage.getItem('payerInfo'));
    }
  }
  getMemberInfo(name){
    let updatePayer = this.payersList.find( payer => payer.payerName === name);    
    let index = this.payersList.indexOf(updatePayer);

    // this._payers.getMemberInformation().subscribe(res=>{
    //   const result = res.body; 
    //   if(res.status == 200){
    //     let gId = res.body.entry[0].resource.id.split('/');
    //     this.payersList[index].groupId = gId[1];
    //     localStorage.setItem('payerInfo',JSON.stringify(this.payersList));
    //   }
    // });
  }

  extractInfo(gId,name){    
    let updatePayer = this.payersList.find( payer => payer.payerName === name);    
    let index = this.payersList.indexOf(updatePayer);
    this._payers.getExtractInformation(gId).subscribe(res=>{
      // const result = res.body; 
      if(res.status == 200){
        let location = res.headers.get('Content-Location');
        this.payersList[index].contentLocation = location;
        localStorage.setItem('payerInfo',JSON.stringify(this.payersList));
      }
    });
  }

}
