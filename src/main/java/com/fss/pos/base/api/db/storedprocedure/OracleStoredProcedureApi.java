package com.fss.pos.base.api.db.storedprocedure;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import com.fss.pos.base.api.db.OracleDataBaseApi;
import com.fss.pos.base.commons.constants.Constants;

@StoredProcedure(Constants.DB_TYPE_ORACLE)
public class OracleStoredProcedureApi extends OracleDataBaseApi implements
		StoredProcedureApi {

	@Override
	@SuppressWarnings("unchecked")
	public List<Object> getBean(List<Object> params, String procedureName,
			List<Class<?>> classList, String mspAcr) throws Exception {
		return (List<Object>) executeStoredProcedure(mspAcr, procedureName,
				classList.size(), params == null ? new ArrayList<Object>()
						: params, classList);
	}

	@Override
	public String getString(List<Object> params, String procedureName,
			String mspAcr) throws Exception {
		return (String) executeStoredProcedure(procedureName,
				params == null ? new ArrayList<Object>() : params, mspAcr);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Object> assignValuesFromResultSet(Object resultSetObject,
			Object object) throws Exception {
		int inc = 0;
		ArrayList<Object> objList = new ArrayList<Object>();
		for (ResultSet rs : (List<ResultSet>) resultSetObject) {
			if (rs == null)
				continue;
			Class<?> clazz = ((List<Class<?>>) object).get(inc);
			ResultSetMetaData rsmt = rs.getMetaData();
			List list = new ArrayList();
			while (rs.next()) {
				Object o = clazz.newInstance();
				iterateResultSet(rs, rsmt, clazz, o);
				list.add(o);
			}
			objList.add(list);
			inc++;
		}
		return objList;
	}

}
