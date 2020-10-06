import { Injectable } from '@angular/core';
import { timer, interval } from 'rxjs';
import { map, tap, retryWhen, delayWhen, groupBy } from 'rxjs/operators';
import { HttpClient, HttpHeaders, } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import 'rxjs/Rx';


@Injectable({
  providedIn: 'root'
})
export class PayersService {
  basePath = environment.apiUrl;
  // userResumeBio:any = [];
  tkn = { };
  constructor(private _http: HttpClient) { }



  getMemberInformation(npi, taxId, payerUrl, groupName, tokenPoint, authValue, identifier, tin, NpI) {
    // let npi = 1316206220;  let taxId = 789456231;
  if(taxId){
    console.log(taxId)
    let queryParams = '/Group?identifier=http://terminology.hl7.org/CodeSystem/v2-0203%7CNPI%7C' + npi + '&identifier=http://terminology.hl7.org/CodeSystem/v2-0203%7CTAX%7C' + taxId + '&_format=json';
    const encodedUri = (payerUrl + queryParams);
    return this._http.get<any>(encodedUri, { observe: 'response' }).pipe(map(result => {
      // console.log(result);
      return result;
    },
      error => {
        console.log(error);
      }
    ));
  }
  else if(groupName!='undefined'){
    if(groupName=='MULTICARE CONNECTED CARE'){
      let body = '{"auditFields":  { "developer":"R630012 Testing" } }';
     this._http.post(tokenPoint,body, { 
        headers:({
          'Authorization' : 'Basic '+authValue,
          'Content-Type': 'application/json',
        }),
  
      })
      .subscribe(data => {
        let greetingsMap = new Map(Object.entries(data));
        let acesstoken = greetingsMap.get('accessToken');
        console.log(acesstoken)
        // this.tkn.push(acesstoken)
        this.tkn = acesstoken
        console.log(this.tkn)
        // localStorage. setItem('acesstoken', acesstoken);
        console.log(data)


    })
        var headers_object = new HttpHeaders({
          'Content-Type': 'application/fhir+json',
           'Authorization': "Bearer "+this.tkn,
        });
        console.log(this.tkn)
        const encodedUri = encodeURI(payerUrl+'/Group?name='+groupName+'&_format=json');
        return this._http.get<any>(encodedUri, {headers: headers_object, observe: "response"})
        .pipe(map(result => {
          console.log(result)
          return result;
        },
          error => {
            console.log(error);
          }
        ));
  }
        
  else if(groupName=='Test Group 3'){
      console.log(groupName)
      let quParams = '/Group?name='+ groupName + '&_format=json';
      const encodUri = encodeURI(payerUrl + quParams);
      return this._http.get<any>(encodUri, { observe: 'response' }).pipe(map(result => {
        console.log(result);
        return result;
      },
        error => {
          console.log(error);
        }
      ));
     
    }
    else if(identifier=='https://sitenv.org%7C1316206220'){
    console.log(identifier)
      let quParams = '/Group?identifier='+ identifier + '&_format=json';
      const encodUri = (payerUrl + quParams);
      return this._http.get<any>(encodUri, { observe: 'response' }).pipe(map(result => {
        console.log(result);
        return result;
      },
        error => {
          console.log(error);
        }
      ));
  }
  else if(tin=='http://terminology.hl7.org/CodeSystem/v2-0203%7CTAX%7C789456231'){
    console.log(tin)
      let quParams = '/Group?identifier='+ tin;
      const encodUri = (payerUrl + quParams);
      return this._http.get<any>(encodUri, { observe: 'response' }).pipe(map(result => {
        console.log(result);
        return result;
      },
        error => {
          console.log(error);
        }
      ));
      // this._http.get(payerUrl+quParams).subscribe(result=>{
      //   console.log(result)
      // })
  }
  else if(NpI=='http://terminology.hl7.org/CodeSystem/v2-0203%7CNPI%7C1316206220'){
    console.log(NpI)
      let quParams = '/Group?identifier='+ NpI + '&_format=json';
      const encodUri = (payerUrl + quParams);
      return this._http.get<any>(encodUri, { observe: 'response' }).pipe(map(result => {
        console.log(result);
        return result;
      },
        error => {
          console.log(error);
        }
      ));
  }
  }
  
}
  getPatientDetails(location) {
    const headers = new HttpHeaders({ 'Accept': 'application/json' });
    const encodeUri = encodeURI(location);
    console.log(encodeUri)
    return this._http.get<any>(encodeUri, {headers: headers, observe: 'response' }).pipe(
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
    const headers = new HttpHeaders({ 'Accept': 'application/fhir+ndjson' });
    const encodeUri = encodeURI(url);
    console.log(encodeUri)
    return this._http.get(encodeUri, { headers: headers, observe: 'response', responseType: 'text' }).pipe(map(result => {
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
