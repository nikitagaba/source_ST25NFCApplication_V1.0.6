/*
 * @author STMicroelectronics MMY Application team
 *
 ******************************************************************************
 * @attention
 *
 * <h2><center>&copy; COPYRIGHT 2017 STMicroelectronics</center></h2>
 *
 * Licensed under ST MIX_MYLIBERTY SOFTWARE LICENSE AGREEMENT (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *        http://www.st.com/Mix_MyLiberty
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied,
 * AND SPECIFICALLY DISCLAIMING THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************
 */

package com.st.st25sdk.tests.type4;

import java.util.Arrays;

import com.st.st25sdk.MultiAreaInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.type4a.STType4Tag;
import com.st.st25sdk.type4a.Type4Tag;

public class STM24TAHighDensityUtils {

    static public void  setNbrOfAreasAndDisablePwdProtections(Type4Tag type4Tag, MultiAreaInterface multiAreaInterface, int nbrOfAreas) throws STException {

        STLog.i("Configure the tag with " + nbrOfAreas + " areas without password protections");

        // We assume that the 128 bits password is null
        byte[] pwd = new byte[16];
        Arrays.fill(pwd, (byte) 0 );

        STType4Tag stType4Tag = (STType4Tag) type4Tag;

        // get the current number of file
        int nbrOfFiles = stType4Tag.getNbrOfFiles();

        // Set each area access rights so that no password is needed in read or in write
        // NB: It is mandatory to do that BEFORE trying to set the number of areas
        for(int fileId = 1; fileId <= nbrOfFiles; fileId++) {
            stType4Tag.unlockRead(fileId, pwd);
            stType4Tag.unlockWrite(fileId, pwd);
        }

        multiAreaInterface.setNumberOfAreas(nbrOfAreas);
    }

}
