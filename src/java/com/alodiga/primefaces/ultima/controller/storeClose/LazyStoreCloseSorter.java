/*
 * Copyright 2009-2014 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alodiga.primefaces.ultima.controller.storeClose;

import com.portal.business.commons.models.StoreClose;
import java.util.Comparator;
import org.primefaces.model.SortOrder;

public class LazyStoreCloseSorter implements Comparator<StoreClose> {

    private String sortField;
    
    private SortOrder sortOrder;
    
    public LazyStoreCloseSorter(String sortField, SortOrder sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }

    public int compare(StoreClose storeClose1, StoreClose storeClose2) {
        try {
            Object value1 = StoreClose.class.getField(this.sortField).get(storeClose1);
            Object value2 = StoreClose.class.getField(this.sortField).get(storeClose2);

            int value = ((Comparable)value1).compareTo(value2);
            
            return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
        }
        catch(Exception e) {
            throw new RuntimeException();
        }
    }
}
