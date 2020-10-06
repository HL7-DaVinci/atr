import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material';

@Component({
  selector: 'app-config',
  templateUrl: './config.component.html',
  styleUrls: ['./config.component.scss']
})
export class ConfigComponent implements OnInit {

  formConfig: FormGroup;
  payersForm: FormGroup;
  formProvider: FormGroup;

  constructor(private formBuilder: FormBuilder, private _snackBar: MatSnackBar) { }

  ngOnInit() {
    // var table = 'providerInfo';
    // if (localStorage.getItem(table) !== null) {
    //   let provider = JSON.parse(localStorage.getItem(table));
    //   this.npi = provider.npi;
    //   this.taxId = provider.taxId;
    // }

    this.preparePayerForm();
    this.addPayerForm();
    // this.prepareFormProvider(this.npi,this.taxId);
  }
  // prepareFormProvider(npi,taxId){
  //   this.formProvider = this.formBuilder.group({
  //     'npi': [npi, Validators.required],
  //     'taxId': [taxId, Validators.required],
  //   });

  // }
  preparePayerForm(){
    this.payersForm = this.formBuilder.group({
      payers: new FormArray([]),
    });
  }
  get fm() { return this.payersForm.controls; }
  get p() { return this.fm.payers as FormArray; }

  addPayerForm(){
    this.p.push(this.formBuilder.group({
      'payerName': ['', Validators.required],
      'payerUrl': [''],
      'tokenPoint':[''],
      'authValue':[''],
      'clientId': [''],
      'tokenEndpoint': [''],
      'jwksUrl': [''],
      'publicKey':[''],
      'privateKey':[''],
      'groupId':[''],
      'contentLocation':[''],
      'bulkData':['']
    }));
  }
  removePayerForm(index){
    if (this.p.length > 1) {
      this.p.removeAt(index);
    }else{
      this._snackBar.open('You cant remove All');
    }
  }
  // openSnackBar() {
  //   this._snackBar.open('Successfully Saved Payer Config Settings!!', 'X', {
  //     duration: 13700,
  //     verticalPosition: 'top',
  //     horizontalPosition: 'end',
  //     panelClass: ['mat-toolbar', 'mat-warn'],
  //   });
  // }

  submitPayer(values){
    if(this.payersForm.valid){
      for(let payer of values.payers){
        console.log('payer', payer)
        var table = 'payerInfo';
        if (localStorage.getItem(table) === null) {
          const dataApi = [payer];
          localStorage.setItem(table,JSON.stringify(dataApi));
        }else{
          const dataApi = JSON.parse(localStorage.getItem(table));
          dataApi.push(payer);
          localStorage.setItem(table,JSON.stringify(dataApi));
        }
      }

      this.preparePayerForm();
      this.addPayerForm();
    } else {
      alert('User form is not valid!!')
    }
  }
  
  // submitProvider(values){
  //   if(this.formProvider.valid){
  //     var table = 'providerInfo';    
  //     localStorage.setItem(table,JSON.stringify(values));
  //     // this.prepareFormProvider(values.npi,values.taxId);
  //     this._snackBar.open('Provider information added', '', {
  //       duration: 2000,
  //     });
  //   } else {
  //     alert('User form is not valid!!')
  //   }
  // }

}
