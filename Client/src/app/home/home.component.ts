import { Component, OnInit, ViewChildren } from '@angular/core';
import { FormGroup, FormBuilder, FormArray, Validators } from '@angular/forms';
import { PayersService } from '../services/payers.service';
import { tap } from 'rxjs/operators';
import { Ng4LoadingSpinnerService } from 'ng4-loading-spinner';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  private selectedLink: string="NPITAXID";  
  selection: string="NPITAXID";      
    setradio(e: string): void{  
        this.selectedLink = e;
    }  
    isSelected(name: string): boolean{  
        if (!this.selectedLink) {
            return false;  
    }  
   return (this.selectedLink === name);   
    }   
    show = false;

  @ViewChildren('patientTable') patientTable;
  payerList: any = [];
  payerViewList = [];
  patientColumnsArr: any = [];
  filteredPatientColumns: any = [];

  constructor(private _payers: PayersService, private spinnerService: Ng4LoadingSpinnerService) { }

  groupColumns: string[] = ['position', 'groupName', 'contractInfo', 'memberInfo', 'action'];
  patientColumns: string[] = ['firstName', 'lastName', 'startDate', 'endDate', 'memberId', 'subscriberId', 'practitioner', 'organization'];
  patientColumnsObj: Array<any> = [
    { name: 'firstName', label: 'First Name', show: true },
    { name: 'lastName', label: 'Last Name', show: true },
    { name: 'startDate', label: 'Start Date', show: true },
    { name: 'endDate', label: 'End Date', show: true },
    { name: 'memberId', label: 'Member Id', show: true },
    { name: 'subscriberId', label: 'Subscriber Id', show: true },
    { name: 'practitioner', label: 'Practitioner', show: true },
    { name: 'organization', label: 'Organization', show: true }
  ];
  ngOnInit() {

    var table = 'payerInfo';
    if (localStorage.getItem(table) !== null) {
      this.payerList = JSON.parse(localStorage.getItem(table));
      // console.log(this.payerList)
    }

    this.addPayerView();
    // localStorage.removeItem('acesstoken')
  }

  removePayerForm(index) {
    if (this.payerViewList.length > 1) {
      let delItem = this.payerViewList.splice(index, 1);
    }
  }
  addPayerView() {
    let count = this.payerViewList.length;
    count++;
    this.patientColumnsArr.push(this.patientColumnsObj);
    this.filteredPatientColumns.push(this.patientColumns);
    this.payerViewList.push({ index: count, groupDetails: [], patientDetails: [] });
    // console.log(this.patientColumns);
  }
  submitPayer(index, form, event: Event) {
    this.spinnerService.show();
    let npi = form.value.npi;
    let taxId = form.value.taxId;
    let payerUrl = form.value.payerName.payerUrl;
    let groupName = form.value.groupName;
    let identifier = form.value.identifier;
    let tin =form.value.tin;
    let NpI = form.value.NpI;
    let tokenPoint = form.value.payerName.tokenPoint;
    let authValue = form.value.payerName.authValue;
    
    // console.log(tokenPoint)
    // console.log(authValue)
    // console.log(groupName)
event.preventDefault();
    this._payers.getMemberInformation(npi, taxId, payerUrl, groupName, tokenPoint, authValue,identifier,tin,NpI).subscribe(res => {
      const result = res.body;
      console.log(result)
      if (res.status == 200) {
        const groupDetails = [];
        console.log(groupDetails)
        var i = 1;
        if (res.body.entry) {
          for (let entry of res.body.entry) {
            var groupContractInfo = '';
            if (entry.resource.characteristic) {
              groupContractInfo = entry.resource.characteristic[0].valueReference ? entry.resource.characteristic[0].valueReference.display ? entry.resource.characteristic[0].valueReference.display : '' : ''
            ||entry.resource.characteristic[0].code ? entry.resource.characteristic[0].code.text ? entry.resource.characteristic[0].code.text : '' : ''
            }
            console.log(groupDetails)
            const obj = {
              position: i,
              groupId: entry.resource.id,
              groupName: entry.resource.name,
              contractInfo: groupContractInfo,
              member: entry.resource.member,
              memberLength: entry.resource.member.length,
              contentLocation: '',
              pArray: []
            };
            console.log(obj)
            groupDetails.push(obj);
            i++;
          }
          // console.log(groupDetails); 
          this.payerViewList[index].groupDetails = groupDetails;
          this.spinnerService.hide();
        } else {
          alert("No Groups were found with the specified data")
        }
      } else {
        alert("Error in getting Group Details")
      }
    });
  }
  getLocation(viewIndex, gId) {
    this.spinnerService.show();
    // console.log(this.payerViewList[viewIndex].groupDetails);
    let updateGroup = this.payerViewList[viewIndex].groupDetails.find(detail => detail.groupId === gId);
    let index = this.payerViewList[viewIndex].groupDetails.indexOf(updateGroup);
    this._payers.getExtractInformation(gId).subscribe(res => {
      if (res.status == 200) {
        let location = res.headers.get('Content-Location');
        // console.log(location);        
        this._payers.getPatientDetails(location).subscribe(res => {
          // console.log(res);
          if (res.status == 200) {
            this.getBulkData(res.body.output, viewIndex, index);
            this.payerViewList[viewIndex].groupDetails[index].contentLocation = location;
            this.spinnerService.hide();
          } else {
            this.spinnerService.hide();
          }
        });
      } else {
        this.spinnerService.hide();
      }

    });

  }
  getPatientDetail(viewIndex, gId) {
    this.spinnerService.show();
    // console.log(viewIndex,gId);
    let updateGroup = this.payerViewList[viewIndex].groupDetails.find(detail => detail.groupId === gId);
    let index = this.payerViewList[viewIndex].groupDetails.indexOf(updateGroup);
    const bulkData = this.payerViewList[viewIndex].groupDetails[index].pArray;
    let pData = bulkData.find(p => p.type == 'Patient');
    const patientDetails = [];
    for (let p of pData.data) {
      console.log(p)

      let member = this.payerViewList[viewIndex].groupDetails[index].member.filter(m => m.entity.reference == 'Patient/' + p.id);
      let startDate = member[0].period.start;
      let coverage = bulkData.filter(c => { if (c.type == 'Coverage') { return true; } });
      let cData = coverage[0].data.filter(cd => cd.subscriber.reference == 'Patient/' + p.id)
      let endDate = cData[0] ? cData[0].period ? cData[0].period.end : '' : '';
      let subscriberId = cData[0] ? cData[0].subscriberId : '';

      let practitioner = bulkData.filter(c => { if (c.type == 'Practitioner') { return true; } });
      console.log(practitioner)
      let prData = practitioner[0].data.filter(pr => 'Practitioner/' + pr.id);
      console.log(prData)
      var organizationName = '';
      if ("managingOrganization" in p && "reference" in p.managingOrganization) {
        let organization = bulkData.filter(c => { if (c.type == 'Organization') { return true; } });
        console.log(organization)
        let orgData = organization[0].data.filter(or => 'Organization/' + or.id == p.managingOrganization.reference);
        console.log(orgData)
        organizationName =  organization[0].data[0].name;
        // console.log(organizationName);
      }
      // console.log(orgData);
      let pObj = {
        id: p.id,
        firstName: p.name[0].given[0],
        lastName: p.name[0].family,
        startDate: startDate,
        endDate: endDate,
        memberId: p.id,
        subscriberId: subscriberId,
        practitioner: prData[0].name[0].given[0] + ' ' + prData[0].name[0].family,
        organization: organizationName
      }
      patientDetails.push(pObj);
    }
    this.payerViewList[viewIndex].patientDetails = patientDetails;
    this.spinnerService.hide();
    // console.log(this.payerViewList[viewIndex].patientDetails);
  }
  getBulkData(outPutData, viewIndex, index) {
    this.spinnerService.show();
    const bulkData = [];
    for (let data of outPutData) {
      // console.log(data.url,'Started');
      this._payers.getBulkData(data.url).subscribe(result => {
        if (result.status == 200) {
          let patients = result.body.split('\n');
          const pArray = [];
          for (let p of patients) {
            pArray.push(JSON.parse(p));
          }
          let obj = {
            type: data.type,
            url: data.url,
            data: pArray
          }
          // console.log(obj);
          // console.log(this.payerViewList);
          this.payerViewList[viewIndex].groupDetails[index].pArray.push(obj);
        }
      });
    }
    this.spinnerService.hide();
  }
  filterColoumns(index, event) {
    this.patientColumnsArr[index].forEach((el, ind) => {
      this.patientColumnsArr[index][ind].show = false;
    });
    event.value.forEach((element, i) => {
      let objectIndex = this.patientColumnsArr[index].findIndex(
        col => col.name === element.name
      );
      this.patientColumnsArr[index][objectIndex].show = true;
    });
    this.filteredPatientColumns[index] = this.getFilteredPatientColumns(index);
  }
  getFilteredPatientColumns(index): string[] {
    return this.patientColumnsArr[index].filter(cd => cd.show).map(cd => cd.name);
  }
}
