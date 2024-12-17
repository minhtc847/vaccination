import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { Container, Row, Col, Button } from 'react-bootstrap';
import { InjectionSchedule } from '../../Interface/BusinessObjectInterface';
import { useDebounce } from "@uidotdev/usehooks";
import { Vaccine } from '../../Interface/BusinessObjectInterface';
import ScheduleDetailModal from './InjectionScheduleDetailModal';
import Paging from '../../Utils/Paging';
import { showErrorAlert } from '../../Utils/ErrorAlert';
import { Search } from '../../Interface/UtilsInterface';
import BASE_URL from '../../Api/BaseApi';
import AlertModal from '../../Modals/AlertModal';
import LoadingOverlay from '../../Utils/LoadingOverlay/LoadingOverlay';

const InjectionScheduleList: React.FC = () => {
    const [schedule, setSchedule] = useState<InjectionSchedule[]>([]);
    const [pageNo, setPageNo] = useState<number>(0);
    const [totalPages, setTotalPages] = useState<number>(0);
    const [searchResult, setSearchResult] = useState<string>("");
    const [pageSize, setPageSize] = useState<number>(5);
    const [totalElements, setTotalElement] = useState<number>(0);
    const [selectedIds, setSelectedIds] = useState<number[]>([]);
    const [selectedSchedule, setSelectedSchedule] = useState<InjectionSchedule | null>(null);
    const [showScheduleModal, setShowScheduleModal] = useState<boolean>(false);
    const [showAlertModal, setShowAlertModal] = useState<boolean>(false);
    const [alertMessage, setAlertMessage] = useState<string>("");
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const debouncedSearchTerm = useDebounce(searchResult, 700);
    const token = localStorage.getItem("token");
    const nav = useNavigate();
    document.title = "List Injection Schedule";

    useEffect(() => {
        setIsLoading(true);
        axios.get(`${BASE_URL}/vaccination/schedule`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
            params: {
                pageNo: pageNo,
                pageSize: pageSize,
                sortBy: 'id',
                sortDir: 'desc',
                nameOrId: debouncedSearchTerm
            },
        })
            .then(response => {
                const result: Search<InjectionSchedule> = response.data;
                setSchedule(result.content);
                setTotalPages(result.totalPaged);
                setTotalElement(result.totalElements);

            })
            .catch(error => {
                if (error.response) {
                    showErrorAlert(() => {
                        nav("/");
                    }, error.response.data.message)
                }
            }).finally(() => {
                setIsLoading(false);
            })
    }, [pageNo, debouncedSearchTerm, pageSize, token, nav]);

    const handlePageChange = (pageNumber: number) => {
        setPageNo(pageNumber - 1);
    };

    const handlePageSizeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        setPageSize(parseInt(e.target.value));
        setPageNo(0); // Reset to the first page when page size changes
    };

    const handleCheckboxChange = (scheduleId: number) => {
        setSelectedIds(prevSelectedIds => {
            if (prevSelectedIds.includes(scheduleId)) {
                return prevSelectedIds.filter(id => id !== scheduleId);
            } else {
                return [...prevSelectedIds, scheduleId];
            }

        });
    };

    const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setSearchResult(e.target.value);
        setPageNo(0); // Reset to the first page when search query changes
    };

    const handleScheduleClick = (schedule: InjectionSchedule) => {
        setSelectedSchedule(schedule);
        setShowScheduleModal(true);
    };

    const handleScheduleModalClose = () => {
        setShowScheduleModal(false);
        setSelectedSchedule(null);
    };

    const formatDate = (dateString: string): string => {
        const date = new Date(dateString);
        const year = date.getFullYear();
        const month = ('0' + (date.getMonth() + 1)).slice(-2); // Adding leading zero and getting month in 2-digit format
        const day = ('0' + date.getDate()).slice(-2); // Adding leading zero and getting day in 2-digit format
        return `${day}/${month}/${year}`;
    }

    const handleUpdateClick = () => {
        if (selectedIds.length === 0) {
            setAlertMessage("No injection schedule selected for update.");
            setShowAlertModal(true);
        } else if (selectedIds.length > 1) {
            setAlertMessage("Please select only one injection schedule for update.");
            setShowAlertModal(true);
        } else {
            nav(`/schedule/update/${selectedIds}`);
        }
    };

    return (
        <Container className='pt-4'>
            {isLoading ? <LoadingOverlay /> : null}
            <h5 className='pb-3' style={{ fontWeight: "bold" }}>INJECTION SCHEDULE LIST</h5>
            {showAlertModal && (
                <AlertModal
                    show={showAlertModal}
                    handleClose={() => setShowAlertModal(false)}
                    message={alertMessage}
                />
            )}
            <Container style={{ backgroundColor: "white" }}>
                <Row className='py-3'>
                    <Col style={{ display: 'flex' }}>
                        Show
                        <div style={{ display: 'inline-block', width: 'auto', margin: '0 10px' }}>
                            <select
                                className="custom-select"
                                onChange={handlePageSizeChange}
                                value={pageSize}
                                style={{ display: 'inline-block', width: 'auto', margin: '0 10px' }}
                            >
                                <option value="5">5</option>
                                <option value="10">10</option>
                                <option value="25">25</option>
                                <option value="50">50</option>
                                <option value="100">100</option>
                            </select>
                        </div>
                        entries
                    </Col>
                    <Col style={{ textAlign: 'end' }}>Search: <input className='custom-search' type='text' onChange={handleSearchChange} /></Col>
                </Row>
                <Container as={Row} className='py-3'>
                    {schedule.length > 0 ?
                        <div className={pageSize !== 5 ? 'tableFixHead' : ''}>
                            <table>
                                <thead>
                                    <tr>
                                        <th></th>
                                        <th>Vaccine</th>
                                        <th>Time</th>
                                        <th>Inject per day</th>
                                        <th>Place</th>
                                        <th>Status</th>
                                        <th>Note</th>
                                    </tr>
                                </thead>

                                <tbody className='text-start'>
                                    {schedule.map(s => (
                                        <tr key={s.id}>
                                            <td className='text-center'><input type='checkbox' checked={selectedIds.includes(s.id)} onChange={() => handleCheckboxChange(s.id)} /></td>
                                            <td >
                                                <div className='custom-id' onClick={() => handleScheduleClick(s)}>
                                                    {s.vaccineName}
                                                </div>
                                            </td>
                                            <td>From <div style={{ display: "inline-block", fontWeight: "bold" }}>{formatDate(s.startDate)}</div> to <div style={{ display: "inline-block", fontWeight: "bold" }}>{formatDate(s.endDate)}</div></td>
                                            <td>{s.injectionTimes}</td>
                                            <td>{s.place?.length > 20 ? `${s.place?.substring(0, 20)}...` : s.place}</td>
                                            <td>{s.status}</td>
                                            <td>{s.description.length > 20 ? `${s.description.substring(0, 20)}...` : s.description}</td>
                                        </tr>
                                    ))}


                                </tbody>
                            </table>
                        </div>
                        : <h3>No data found!</h3>}
                </Container>
                <Row className='py-3'>
                    <Col style={{ textAlign: 'start' }}>Showing {pageNo * pageSize + 1} to {(pageNo + 1) * pageSize} of {totalElements} entries</Col>
                    <Col style={{ textAlign: 'end' }}>
                        <Paging currentPage={pageNo} totalPages={totalPages} onPageChange={handlePageChange} />
                    </Col>
                </Row>
                <Row>
                    <Col style={{ textAlign: 'start' }} className='py-2'>
                        <Button className='m-1 rounded-0' style={{ backgroundColor: '#009688', borderColor: '#009688' }}
                            onClick={() => nav(`/schedule/create`)}>
                            New Injection Schedule
                        </Button>
                        <Button className='m-1 rounded-0 text-white' variant='warning' onClick={handleUpdateClick}>
                            Update Injection Schedule
                        </Button>
                    </Col>
                </Row>
            </Container>
            {selectedSchedule && (
                <ScheduleDetailModal show={showScheduleModal} handleClose={handleScheduleModalClose}
                    schedule={selectedSchedule} />
            )}
        </Container>
    )
}

export default InjectionScheduleList
