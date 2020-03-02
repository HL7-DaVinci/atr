import { Injectable } from '@angular/core';
import { timer, interval } from 'rxjs';
import { map, tap, retryWhen, delayWhen } from 'rxjs/operators';
import { HttpClient, HttpHeaders, } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PayersService {
  basePath = environment.apiUrl;
  constructor(private _http: HttpClient) { }

  getMemberInformation(npi, taxId, payerUrl) {
    // let npi = 1316206220;  let taxId = 789456231;
    let queryParams = '/Group?identifier=http://terminology.hl7.org/CodeSystem/v2-0203|NPI|' + npi + '&identifier=http://terminology.hl7.org/CodeSystem/v2-0203|TAX|' + taxId + '&_format=json';
    const encodedUri = encodeURI(payerUrl + queryParams);
    return this._http.get<any>(encodedUri, { observe: 'response' }).pipe(map(result => {
      // console.log(result);
      return result;
    },
      error => {
        console.log(error);
      }
    ));
  }
  getPatientDetails(location) {
    const encodeUri = encodeURI(location);
    return this._http.get<any>(encodeUri, { observe: 'response' }).pipe(
      map(result => {
        if (result.status === 202) {
          throw result.status;
        }
        return result;
      }),
      retryWhen(errors => errors.pipe(
        //log error message
        tap(result => console.log('Re-try')),
        delayWhen(result => timer(1 * 1000))
      )
      )
    );

    //   .pipe(map(result => {
    //     if(result.status == 200){
    //       return result;
    //     }else if(result.status == 202){
    //       console.log('202');
    //       return this.getPatientDetails(location).pipe(map(result => { return result;}));
    //     }
    //   }).retryWhen(obs => {
    //     return obs; // always just resubscribe without any further logic
    // }));
  }
  getBulkData(url) {
    const encodeUri = encodeURI(url);
    return this._http.get(encodeUri, { observe: 'response', responseType: 'text' }).pipe(map(result => {
      return result;
    }))
  }
  // getBulkData(url): Promise<result[]>{
  //   const encodeUri = encodeURI(url);
  //   return this._http.get(encodeUri, {observe: 'response', responseType: 'text' })
  //   .toPromise()
  //   .then(response => response as result[])
  //   .catch(Error = > console.log(error));
  // return this._http.get(encodeUri, {observe: 'response', responseType: 'text' }).pipe(tap(result => {
  //   return result;
  // }))
  // }
  // async getBulkData(url): Promise<any> {
  //   try {
  //     let response = await this._http.get(url, {observe: 'response', responseType: 'text' }).toPromise();
  //     return response;
  //   } catch (error) {
  //     // await this.handleError(error);
  //   }
  // }

  getExtractInformation(id) {
    const headers = new HttpHeaders({ 'Prefer': 'respond-async', 'Accept': 'application/fhir+json' });

    let url = '/bulk-api-r4/fhir/Group/' + id + '/$export?_type=Patient,Practitioner,Organization,Location,Coverage,RelatedPerson,PractitionerRole';
    const encodedUri = encodeURI(this.basePath + url);
    return this._http.get<any>(encodedUri, { headers: headers, observe: 'response' }).pipe(map(result => {
      // console.log(result);
      return result;
    },
      error => {
        console.log(error);
      }
    ));
  }
  // getBulkData(url) {
  //   const promise = new Promise((resolve, reject) => {
  //     this._http.get(url, {observe: 'response', responseType: 'text' })
  //       .toPromise()
  //       .then((res: any) => {         
  //         resolve(res);
  //       },
  //         err => {
  //           reject(err);
  //         }
  //       );
  //   });
  //   return promise;
  // }
}
