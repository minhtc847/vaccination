import React from 'react';
import { Pagination } from 'react-bootstrap';
import { PagingProps } from '../Interface/UtilsInterface';

const Paging: React.FC<PagingProps> = ({ currentPage, totalPages, onPageChange }) => {

    const renderPaginationItems = () => {
        const pages = [];
        const maxPagesToShow = 3;

        if (totalPages <= maxPagesToShow) {
            for (let i = 1; i <= totalPages; i++) {
                pages.push(
                    <Pagination.Item key={i} active={i === currentPage + 1} onClick={() => onPageChange(i)}>
                        {i}
                    </Pagination.Item>
                );
            }
        } else {
            let startPage = Math.max(1, currentPage + 1 - Math.floor(maxPagesToShow / 2));
            let endPage = Math.min(totalPages, startPage + maxPagesToShow - 1);

            if (endPage - startPage < maxPagesToShow - 1) {
                startPage = Math.max(1, endPage - maxPagesToShow + 1);
            }

            if (startPage > 1) {
                pages.push(
                    <Pagination.Item key={1} onClick={() => onPageChange(1)}>
                        1
                    </Pagination.Item>
                );
                if (startPage > 2) {
                    pages.push(<Pagination.Ellipsis key="start-ellipsis" />);
                }
            }

            for (let i = startPage; i <= endPage; i++) {
                pages.push(
                    <Pagination.Item key={i} active={i === currentPage + 1} onClick={() => onPageChange(i)}>
                        {i}
                    </Pagination.Item>
                );
            }

            if (endPage < totalPages) {
                if (endPage < totalPages - 1) {
                    pages.push(<Pagination.Ellipsis key="end-ellipsis" />);
                }
                pages.push(
                    <Pagination.Item key={totalPages} onClick={() => onPageChange(totalPages)}>
                        {totalPages}
                    </Pagination.Item>
                );
            }
        }

        return pages;
    };

    return (
        <Pagination className="custom-pagination" style={{ justifyContent: 'end' }}>
            <Pagination.Prev onClick={() => onPageChange(currentPage)} disabled={currentPage === 0} />
            {renderPaginationItems()}
            <Pagination.Next onClick={() => onPageChange(currentPage + 2)} disabled={currentPage === totalPages - 1} />
        </Pagination>
    );
}

export default Paging;
